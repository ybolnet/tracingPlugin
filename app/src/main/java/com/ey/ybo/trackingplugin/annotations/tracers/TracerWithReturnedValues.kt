package com.ey.ybo.trackingplugin.annotations.tracers

import android.util.Log
import com.ey.ybo.trackinglib.tracer.Tracer

class TracerWithReturnedValues : Tracer {
    override fun trace(
        defaultMessage: String,
        java: Boolean,
        methodName: String,
        parameterValues: Array<Any?>,
    ) {
        println("Tracedo $methodName vs " + Returner().javaClass.name)
        if (methodName.startsWith(Returner().javaClass.name)) {
            if (parameterValues.size != 2) {
                throw Error("tracking error")
            }
            val returnedObject = parameterValues[0]
            val callingMethod = parameterValues[1]
            Log.d("TRACING_WITH_RETURNS", "Tracedo $callingMethod returning $returnedObject")
        } else {
            Log.d("TRACING_WITH_RETURNS", defaultMessage)
        }
    }
}
