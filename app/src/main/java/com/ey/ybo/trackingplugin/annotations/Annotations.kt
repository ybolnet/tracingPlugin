package com.ey.ybo.trackingplugin.annotations

import com.ybo.trackingplugin.tracerlib.Tracer
import com.ybo.trackingplugin.tracerlib.Tracer.TraceHistoryManagementAction.None
import com.ybo.trackingplugin.tracerlib.defaulttracer.ReturnTrace
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.ReturnValueTracer
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.Returner

annotation class Trace()
annotation class Bullshit()

fun <T> Any?.withTrace(): T {
    return ExampleReturner().trace(this as T)
}

class ExampleReturner() : Returner() {
    @ReturnTrace
    override fun <T> onAnnotateThisToTrace(toTrace: T, callingMethod: String): T {
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
        history: List<Tracer.Method?>,
        returnedObject: Any?,
    ): Tracer.TraceHistoryManagementAction {
        println("returning from $methodReturning $returnedObject")
        return None
    }
}
