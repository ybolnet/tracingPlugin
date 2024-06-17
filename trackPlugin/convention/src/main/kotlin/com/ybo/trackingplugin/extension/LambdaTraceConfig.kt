package com.ybo.trackingplugin.extension

import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.DefaultLambdaTracerFactory

data class LambdaTraceConfig(
    var tracerFactory: String = DefaultLambdaTracerFactory::class.java.canonicalName,
)
