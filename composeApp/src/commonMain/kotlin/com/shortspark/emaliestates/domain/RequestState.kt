package com.shortspark.emaliestates.domain

sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T) : RequestState<T>()
    data class Error(val message: String, val code: Int? = null) : RequestState<Nothing>()

    fun isIdle(): Boolean = this is Idle
    fun isLoading(): Boolean = this is Loading
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error

    fun getDataOrNull(): T? = (this as? Success)?.data
    fun getErrorOrNull(): Error? = this as? Error

    // Inline helper for safe data access
    inline fun onSuccess(action: (T) -> Unit): RequestState<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Error) -> Unit): RequestState<T> {
        if (this is Error) action(this)
        return this
    }

    inline fun onLoading(action: () -> Unit): RequestState<T> {
        if (this is Loading) action()
        return this
    }
}