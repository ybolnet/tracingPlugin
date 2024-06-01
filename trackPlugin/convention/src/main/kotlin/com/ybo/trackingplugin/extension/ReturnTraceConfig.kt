package com.ybo.trackingplugin.extension

import com.ybo.trackingplugin.tracerlib.defaulttracer.tracers.DefaultReturnValueTracerFactory

data class ReturnTraceConfig(
    var tracerFactory: String = DefaultReturnValueTracerFactory::class.java.canonicalName,
)
