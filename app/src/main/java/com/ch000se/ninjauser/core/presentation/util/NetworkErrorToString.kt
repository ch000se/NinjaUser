package com.ch000se.ninjauser.core.presentation.util

import android.content.Context
import com.ch000se.ninjauser.R
import com.ch000se.ninjauser.core.domain.util.NetworkError

fun NetworkError.asString(context: Context): String {
    val resId = when (this) {
        NetworkError.REQUEST_TIMEOUT -> R.string.error_request_timeout
        NetworkError.NO_INTERNET -> R.string.error_no_internet
        NetworkError.SERVER_ERROR -> R.string.error_server
        NetworkError.SERIALIZATION -> R.string.error_serialization
        NetworkError.UNKNOWN -> R.string.error_unknown
    }
    return context.getString(resId)
}