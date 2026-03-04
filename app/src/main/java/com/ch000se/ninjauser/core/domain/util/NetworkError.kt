package com.ch000se.ninjauser.core.domain.util

enum class NetworkError {
    REQUEST_TIMEOUT,
    NO_INTERNET,
    SERVER_ERROR,
    SERIALIZATION,
    UNKNOWN
}

fun Throwable?.toNetworkError(): NetworkError {
    return when (this) {
        is java.net.SocketTimeoutException -> NetworkError.REQUEST_TIMEOUT
        is java.net.UnknownHostException -> NetworkError.NO_INTERNET
        is java.net.ConnectException -> NetworkError.NO_INTERNET
        is kotlinx.serialization.SerializationException -> NetworkError.SERIALIZATION
        is retrofit2.HttpException -> {
            when (code()) {
                in 500..599 -> NetworkError.SERVER_ERROR
                else -> NetworkError.UNKNOWN
            }
        }

        else -> NetworkError.UNKNOWN
    }
}