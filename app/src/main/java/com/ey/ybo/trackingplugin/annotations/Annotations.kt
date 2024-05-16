package com.ey.ybo.trackingplugin.annotations

import com.ybo.trackingplugin.tracerlib.Tracer
import com.ybo.trackingplugin.tracerlib.defaulttracer.ReturnTrace
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.ReturnValueTracer
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.Returner

annotation class Trace()
annotation class UnTrace()
annotation class Bullshit()

fun <T> Any.withTrace(): T {
    val throwable = Throwable()
    val stackElement = throwable.stackTrace.run {
        if (size >= 2) {
            this[1]
        } else {
            null
        }
    }
    val fullMethodName = (stackElement?.className ?: "") + "." + (stackElement?.methodName ?: "")
    return ReturnerImpl().traceReturning(this as T, fullMethodName)
}

class ReturnerImpl : Returner {
    @ReturnTrace
    override fun <T> traceReturning(toTrace: T, callingMethod: String): T {
        return toTrace
    }
}

class ReturnTracerFactory : Tracer.Factory {
    override fun create() = ReturnTracer()
}

class ReturnTracer : ReturnValueTracer() {
    override fun traceReturn(
        defaultMessage: String,
        annotationName: String,
        methodReturning: String?,
        history: List<Tracer.Method>,
        returnedObject: Any?,
    ): Boolean {
        println("returning from $methodReturning $returnedObject")
        return false
    }
}
