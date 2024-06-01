package com.ey.ybo.trackingplugin.annotations

import com.ybo.trackingplugin.tracerlib.defaulttracer.ReturnTrace
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.Returner

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
