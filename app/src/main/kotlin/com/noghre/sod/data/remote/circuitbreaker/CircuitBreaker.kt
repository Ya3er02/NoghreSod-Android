package com.noghre.sod.data.remote.circuitbreaker

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

/**
 * Circuit Breaker Pattern Implementation
 * 
 * Prevents cascade failures by monitoring endpoint health
 * 
 * States:
 * - CLOSED: Normal operation, requests pass through
 * - OPEN: Too many failures, requests blocked
 * - HALF_OPEN: Testing if service recovered
 * 
 * @since 1.0.0
 */
@Singleton
class CircuitBreaker @Inject constructor(
    private val config: CircuitBreakerConfig = CircuitBreakerConfig()
) {
    
    sealed class State {
        object Closed : State()      // Requests passing through
        object Open : State()        // Requests blocked
        object HalfOpen : State()    // Testing recovery
    }
    
    data class CircuitState(
        var state: State = State.Closed,
        var failureCount: Int = 0,
        var successCount: Int = 0,
        var lastFailureTime: Long = 0,
        var lastStateChangeTime: Long = System.currentTimeMillis(),
        val failureWindow: ArrayDeque<Long> = ArrayDeque(100)
    )
    
    data class CircuitBreakerConfig(
        val failureThreshold: Int = 5,           // Open after N failures
        val successThreshold: Int = 2,           // Close after N successes
        val timeout: Long = 60000,               // Time in OPEN state (1 min)
        val windowSize: Long = 30000,            // Sliding window (30s)
        val halfOpenMaxRequests: Int = 3         // Max requests in HALF_OPEN
    )
    
    private val states = ConcurrentHashMap<String, CircuitState>()
    private val lock = Mutex()
    
    suspend fun allowRequest(endpoint: String): Boolean = lock.withLock {
        val circuitState = states.getOrPut(endpoint) { CircuitState() }
        
        return when (circuitState.state) {
            State.Closed -> true
            
            State.Open -> {
                val now = System.currentTimeMillis()
                if (now - circuitState.lastStateChangeTime >= config.timeout) {
                    // Transition to HALF_OPEN
                    circuitState.state = State.HalfOpen
                    circuitState.successCount = 0
                    circuitState.lastStateChangeTime = now
                    
                    Timber.i("🔄 Circuit breaker for $endpoint: OPEN → HALF_OPEN")
                    true
                } else {
                    false
                }
            }
            
            State.HalfOpen -> {
                // Allow limited requests
                circuitState.successCount + circuitState.failureCount < config.halfOpenMaxRequests
            }
        }
    }
    
    suspend fun recordSuccess(endpoint: String) = lock.withLock {
        val circuitState = states.getOrPut(endpoint) { CircuitState() }
        
        when (circuitState.state) {
            State.Closed -> {
                // Reset failure count
                circuitState.failureCount = 0
                circuitState.failureWindow.clear()
            }
            
            State.HalfOpen -> {
                circuitState.successCount++
                
                // Close if threshold reached
                if (circuitState.successCount >= config.successThreshold) {
                    circuitState.state = State.Closed
                    circuitState.failureCount = 0
                    circuitState.successCount = 0
                    circuitState.lastStateChangeTime = System.currentTimeMillis()
                    
                    Timber.i("✅ Circuit breaker for $endpoint: HALF_OPEN → CLOSED")
                }
            }
            
            State.Open -> {
                // Ignore success in OPEN state
            }
        }
    }
    
    suspend fun recordFailure(endpoint: String) = lock.withLock {
        val circuitState = states.getOrPut(endpoint) { CircuitState() }
        val now = System.currentTimeMillis()
        
        // Add to sliding window
        circuitState.failureWindow.addLast(now)
        circuitState.lastFailureTime = now
        
        // Remove old failures outside window
        while (circuitState.failureWindow.isNotEmpty() && 
               now - circuitState.failureWindow.first() > config.windowSize) {
            circuitState.failureWindow.removeFirst()
        }
        
        circuitState.failureCount = circuitState.failureWindow.size
        
        when (circuitState.state) {
            State.Closed -> {
                if (circuitState.failureCount >= config.failureThreshold) {
                    circuitState.state = State.Open
                    circuitState.lastStateChangeTime = now
                    
                    Timber.w("🔴 Circuit breaker for $endpoint: CLOSED → OPEN (${circuitState.failureCount} failures)")
                }
            }
            
            State.HalfOpen -> {
                // One failure returns to OPEN
                circuitState.state = State.Open
                circuitState.failureCount++
                circuitState.successCount = 0
                circuitState.lastStateChangeTime = now
                
                Timber.w("🔴 Circuit breaker for $endpoint: HALF_OPEN → OPEN")
            }
            
            State.Open -> {
                circuitState.failureCount++
            }
        }
    }
    
    suspend fun getState(endpoint: String): State = lock.withLock {
        return states[endpoint]?.state ?: State.Closed
    }
    
    suspend fun reset(endpoint: String) = lock.withLock {
        states.remove(endpoint)
        Timber.i("🔄 Circuit breaker for $endpoint: RESET")
    }
    
    suspend fun resetAll() = lock.withLock {
        states.clear()
        Timber.i("🔄 All circuit breakers: RESET")
    }
    
    suspend fun getMetrics(): Map<String, CircuitMetrics> = lock.withLock {
        return states.mapValues { (_, state) ->
            CircuitMetrics(
                state = state.state,
                failureCount = state.failureCount,
                successCount = state.successCount,
                lastFailureTime = state.lastFailureTime
            )
        }
    }
}

data class CircuitMetrics(
    val state: CircuitBreaker.State,
    val failureCount: Int,
    val successCount: Int,
    val lastFailureTime: Long
)
