package com.noghre.sod.domain.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultTest {
    @Test
    fun `Success result should return data`() {
        val result = Result.Success("test")
        assertTrue(result.isSuccess)
        assertFalse(result.isError)
        assertFalse(result.isLoading)
        assertEquals("test", result.getOrNull())
    }

    @Test
    fun `Error result should contain exception`() {
        val exception = RuntimeException("Test error")
        val result = Result.Error(exception)
        assertTrue(result.isError)
        assertFalse(result.isSuccess)
        assertFalse(result.isLoading)
        assertNull(result.getOrNull())
    }

    @Test
    fun `Loading result should have isLoading true`() {
        val result: Result<String> = Result.Loading
        assertTrue(result.isLoading)
        assertFalse(result.isSuccess)
        assertFalse(result.isError)
    }

    @Test
    fun `map should transform Success data`() {
        val result: Result<Int> = Result.Success(5)
        val mapped = result.map { it * 2 }
        assertEquals(10, mapped.getOrNull())
    }

    @Test
    fun `map should preserve Error`() {
        val exception = RuntimeException("Test")
        val result: Result<Int> = Result.Error(exception)
        val mapped = result.map { it * 2 }
        assertTrue(mapped.isError)
    }

    @Test
    fun `onSuccess should execute for Success`() {
        var executed = false
        Result.Success("data").onSuccess { executed = true }
        assertTrue(executed)
    }

    @Test
    fun `onSuccess should not execute for Error`() {
        var executed = false
        Result.Error(RuntimeException()).onSuccess { executed = true }
        assertFalse(executed)
    }

    @Test
    fun `onError should execute for Error`() {
        var executed = false
        Result.Error(RuntimeException()).onError { executed = true }
        assertTrue(executed)
    }

    @Test
    fun `onError should not execute for Success`() {
        var executed = false
        Result.Success("data").onError { executed = true }
        assertFalse(executed)
    }
}
