package com.noghre.sod.data.remote.resilience

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.ArrayDeque
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🔛 Circuit Breaker Pattern
 * 
 * Prevents cascading failures by monitoring endpoints.
 * States: Closed (normal) -> Open (fail fast) -> HalfOpen (testing)
 * 
 * @since 1.0.0
 */
@Singleton
class CircuitBreaker @Inject constructor(
    private val config: CircuitBreakerConfig = CircuitBreakerConfig()
) {
    
    /**
     * Circuit breaker states
     */
    sealed class State {
        object Closed : State()    // Normal operation
        object Open : State()      // Rejecting requests
        object HalfOpen : State()  // Testing recovery
    }
    
    /**
     * Circuit state per endpoint
     */
    private data class CircuitState(
        var state: State = State.Closed,
        var failureCount: Int = 0,
        var successCount: Int = 0,
        var lastFailureTime: Long = 0,
        var lastStateChangeTime: Long = System.currentTimeMillis(),
        val failureWindow: ArrayDeque<Long> = ArrayDeque(100)
    )
    
    /**
     * Circuit breaker configuration
     */
    data class CircuitBreakerConfig(
        val failureThreshold: Int = 5,        // Failures to open
        val successThreshold: Int = 2,        // Successes to close
        val timeout: Long = 60000,            // Time to test (1 min)
        val windowSize: Long = 30000,         // Sliding window (30s)
        val halfOpenMaxRequests: Int = 3      // Requests in half-open
    )
    
    private val states = ConcurrentHashMap<String, CircuitState>()
    private val lock = Mutex()
    
    /**
     * Check if request is allowed
     */
    suspend fun allowRequest(endpoint: String): Boolean = lock.withLock {
        val circuitState = states.getOrPut(endpoint) { CircuitState() }
        
        return when (circuitState.state) {
            // Closed: allow all requests
            State.Closed -> true
            
            // Open: reject except after timeout
            State.Open -> {
                val now = System.currentTimeMillis()
                if (now - circuitState.lastStateChangeTime >= config.timeout) {
                    // Transition to HalfOpen
                    circuitState.state = State.HalfOpen
                    circuitState.successCount = 0
                    circuitState.lastStateChangeTime = now
                    
                    Timber.i("🟢 Circuit breaker $endpoint: OPEN -> HALF_OPEN")
                    true
                } else {
                    false
                }
            }
            
            // HalfOpen: allow limited requests
            State.HalfOpen -> {
                circuitState.successCount + circuitState.failureCount < config.halfOpenMaxRequests
            }
        }
    }
    
    /**
     * Record successful request
     */
    suspend fun recordSuccess(endpoint: String) = lock.withLock {
        val circuitState = states.getOrPut(endpoint) { CircuitState() }
        
        when (circuitState.state) {
            State.Closed -> {
                // Clear failures
                circuitState.failureCount = 0
                circuitState.failureWindow.clear()
            }
            
            State.HalfOpen -> {
                circuitState.successCount++
                
                // If threshold reached, close
                if (circuitState.successCount >= config.successThreshold) {
                    circuitState.state = State.Closed
                    circuitState.failureCount = 0
                    circuitState.successCount = 0
                    circuitState.lastStateChangeTime = System.currentTimeMillis()
                    
                    Timber.i("🟢 Circuit breaker $endpoint: HALF_OPEN -> CLOSED")
                }
            }
            
            State.Open -> {}
        }
    }
    
    /**
     * Record failed request
     */
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
                // Check threshold
                if (circuitState.failureCount >= config.failureThreshold) {
                    circuitState.state = State.Open
                    circuitState.lastStateChangeTime = now
                    
                    Timber.w("🔴 Circuit breaker $endpoint: CLOSED -> OPEN (failures: ${circuitState.failureCount})")
                }
            }
            
            State.HalfOpen -> {
                // One failure reopens
                circuitState.state = State.Open
                circuitState.failureCount++
                circuitState.successCount = 0
                circuitState.lastStateChangeTime = now
                
                Timber.w("🔴 Circuit breaker $endpoint: HALF_OPEN -> OPEN")
            }
            
            State.Open -> {
                circuitState.failureCount++
            }
        }
    }
    
    /**
     * Get current state
     */
    fun getState(endpoint: String): State {
        return states[endpoint]?.state ?: State.Closed
    }
    
    /**
     * Check if endpoint is open
     */
    fun isOpen(endpoint: String): Boolean {
        return getState(endpoint) is State.Open
    }
    
    /**
     * Check if endpoint is half-open
     */
    fun isHalfOpen(endpoint: String): Boolean {
        return getState(endpoint) is State.HalfOpen
    }
    
    /**
     * Reset circuit for endpoint
     */
    suspend fun reset(endpoint: String) = lock.withLock {
        states.remove(endpoint)
        Timber.i("🔘 Circuit breaker $endpoint: RESET")
    }
    
    /**
     * Reset all circuits
     */
    suspend fun resetAll() = lock.withLock {
        states.clear()
        Timber.i("🔘 All circuit breakers: RESET")
    }
    
    /**
     * Get all states
     */
    fun getAllStates(): Map<String, State> {
        return states.mapValues { it.value.state }
    }
}
