package com.shortspark.emaliestates.domain

sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T) : RequestState<T>()
    data class Message(val message: String) : RequestState<Nothing>()
    data class Meta<out T>(val meta: T): RequestState<T>()
    data class Error<out T>(val error: T) : RequestState<T>()

    fun isLoading(): Boolean = this is Loading
    fun isError(): Boolean = this is Error
    fun isSuccess(): Boolean = this is Success

    fun getSuccessData() = (this as? Success)?.data
    fun getMessageData() = (this as? Message)?.message
    fun getMetaData() = (this as? Meta)?.meta
    fun getErrorData() = (this as? Error)?.error
}