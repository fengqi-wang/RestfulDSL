package com.ktfan.retrodsl.model

import kotlinx.coroutines.Deferred
import retrofit2.Response

sealed class AsyncResult<out T : Any>

data class AsyncSuccess<out T : Any>(val data: T?) : AsyncResult<T>()

data class AsyncError(val error: String?) : AsyncResult<Nothing>()

inline fun <T : Any> AsyncResult<T>.onSuccess(action: (T?) -> Unit): AsyncResult<T> {
    if (this is AsyncSuccess) action(data)

    return this
}

inline fun <T : Any> AsyncResult<T>.onError(action: (String) -> Unit) {
    if (this is AsyncError && error != null) action(error)
}

suspend fun <T : Any> callAsync(block: () -> Deferred<Response<T>>): AsyncResult<T> {
    return try {
        val response = block().await()
        if (response.isSuccessful)
            AsyncSuccess(response.body())
        else
            AsyncError(response.errorBody()?.string())
    } catch (e: Exception) {
        AsyncError(e.message)
    }
}