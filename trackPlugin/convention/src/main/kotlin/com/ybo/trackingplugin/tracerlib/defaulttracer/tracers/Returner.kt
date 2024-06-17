package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

/**
 * part of the process to trace return value.
 * Implement this
 */
abstract class Returner {

    fun <T> trace(returnValue: T): T {
        val stackElement = Throwable().stackTrace.run {
            if (size >= 3) {
                this[2]
            } else {
                null
            }
        }
        val fullMethodName =
            (stackElement?.className ?: "") + "." + (stackElement?.methodName ?: "")
        return onAnnotateThisToTrace(returnValue, fullMethodName)
    }

    abstract fun <T> onAnnotateThisToTrace(toTrace: T, callingMethod: String): T
}