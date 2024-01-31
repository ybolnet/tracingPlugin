package com.ey.ybo.trackinglib.tracer

interface Tracer {

    fun trace(
        defaultMessage: String,
        java: Boolean,
        methodName: String,
        parameterValues: Array<Any?>,
    )

    interface Factory {
        fun create(): Tracer
    }
}
