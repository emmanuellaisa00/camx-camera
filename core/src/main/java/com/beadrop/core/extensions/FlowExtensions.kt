package com.beadrop.core.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.collectLatest
import com.beadrop.core.util.AppResult

fun <T> Flow<T>.asResult(): Flow<AppResult<T>> {
    return this
        .map<T, AppResult<T>> { AppResult.Success(it) }
        .onStart { emit(AppResult.Loading) }
        .catch { emit(AppResult.Error(it)) }
}

fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime >= windowDuration) {
            lastEmissionTime = currentTime
            emit(value)
        }
    }
}

fun <T> Flow<T>.throttleLatest(windowDuration: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collectLatest { value ->
        val currentTime = System.currentTimeMillis()
        val delay = windowDuration - (currentTime - lastEmissionTime)
        if (delay > 0) {
            kotlinx.coroutines.delay(delay)
        }
        lastEmissionTime = System.currentTimeMillis()
        emit(value)
    }
}
