package com.ey.ybo.trackingplugin.annotations

import com.ybo.trackingplugin.tracerlib.Tracer
import com.ybo.trackingplugin.tracerlib.Tracer.TraceHistoryManagementAction.None
import com.ybo.trackingplugin.tracerlib.defaulttracer.ReturnTrace
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.ReturnValueTracer
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.Returner
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.SimplestDefaultTracer

annotation class Trace()
annotation class Bullshit()

annotation class Cocomerlo()

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
        methodReturning: ReturningContext,
        history: List<Tracer.Method?>,
        returnedObject: Any?,
    ): Tracer.TraceHistoryManagementAction {
        println("returning from $methodReturning $returnedObject")
        return None
    }
}

class BullshitTracerFactory : Tracer.Factory {
    override fun create(): Tracer {
        return SimplestDefaultTracer()
    }
}
