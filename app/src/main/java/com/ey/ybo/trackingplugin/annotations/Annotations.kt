package com.ey.ybo.trackingplugin.annotations

import com.ybo.trackingplugin.tracerlib.defaulttracer.LambdaTrace
import com.ybo.trackingplugin.tracerlib.defaulttracer.ReturnTrace
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.LambdaReturner
import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.Returner

annotation class Trace()
annotation class Bullshit()

annotation class Cocomerlo()

class Special {
    annotation class Tracing
}

fun <T> Any?.withTrace(): T {
    return ExampleReturner().trace(this as T)
}

fun <T : Function<*>> Function<*>.lambdaWithTrace(): T {
    return ExampleLambdaReturner().trace(this as T)
}

class ExampleLambdaReturner() : LambdaReturner() {

    @LambdaTrace
    override fun <T> onAnnotateThisToTrace(
        toTrace: T,
        contextMethod: String,
        callingMethod: String,
        params: List<Any?>,
    ): T {
        return toTrace
    }
}

class ExampleReturner() : Returner() {
    @ReturnTrace
    override fun <T> onAnnotateThisToTrace(toTrace: T, callingMethod: String): T {
        return toTrace
    }
}
