package com.ey.ybo.trackingplugin.annotations.tracers

import android.util.Log
import com.ey.ybo.trackinglib.tracer.Tracer

class TracerSimple : Tracer {
    override fun trace(
        defaultMessage: String,
        java: Boolean,
        methodName: String,
        parameterValues: Array<Any?>,
    ) {
        Log.d("TRACING_SIMPLE", defaultMessage)
    }
}
