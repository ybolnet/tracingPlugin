package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

import com.ybo.trackingplugin.tracerlib.Tracer

/**
 * tracer specialized for the return value of methods.
 * in the config{} closure of the plugin, use
 *
 *          addReturnValueConfig {
 *             tracerFactory = "HERE PUT FACTORY CREATING A SUBTYPE OF ReturnValueTracer"
 *         }
 */
abstract class ReturnValueTracer : Tracer {

    abstract fun traceReturn(
        defaultMessage: String,
        annotationName: String,
        methodReturning: String?,
        history: List<Tracer.Method>,
        returnedObject: Any?,
    ): Boolean

    override fun trace(
        defaultMessage: String,
        java: Boolean,
        annotationName: String,
        method: Tracer.Method,
        history: List<Tracer.Method>,
        parameterValues: Array<Any?>,
    ): Boolean {
        if (parameterValues.size != 2) {
            throw Error("tracking error")
        }
        val returnedObject = parameterValues[0]
        val callingMethod =
            history.getMethodFromHistory(parameterValues[1] as String)?.originalName
                ?: parameterValues[1] as String
        return traceReturn(defaultMessage, annotationName, callingMethod, history, returnedObject)
    }

    fun List<Tracer.Method>.getMethodFromHistory(methodObf: String): Tracer.Method? {
        return this.find { it.possiblyObfuscatedMethod == methodObf }
    }
}

interface Returner {
    fun <T> traceReturning(toTrace: T, callingMethod: String): T
}
