package com.ey.ybo.trackingplugin.annotations.tracers

import com.ey.ybo.trackingplugin.annotations.ToTrace

fun <T> Any.withTrace(): T {
    val throwable = Throwable()
    val stackElement = throwable.stackTrace.run {
        if (size >= 2) {
            this[1]
        } else {
            null
        }
    }
    val fullMethodName = (stackElement?.className ?: "") + "." +  (stackElement?.methodName ?: "")
    return Returner().traceReturning(this as T, fullMethodName)
}

class Returner {
    @ToTrace
    fun <T> traceReturning(toTrace: T, callingMethod: String): T {
        return toTrace
    }
}
