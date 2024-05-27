package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

import com.ybo.trackingplugin.tracerlib.Tracer
import com.ybo.trackingplugin.tracerlib.Tracer.TraceHistoryManagementAction

/**
 * tracer specialized for the return value of methods.
 * in the config{} closure of the plugin, use
 *
 *          addReturnValueConfig {
 *             tracerFactory = "HERE PUT FACTORY CREATING A SUBTYPE OF ReturnValueTracer"
 *         }
 */
abstract class ReturnValueTracer : Tracer {

    /**
     * override this to be warned of a method returning,
     * giving you the opportunity to trace it
     */
    abstract fun traceReturn(
        defaultMessage: String,
        annotationName: String,
        methodReturning: String?,
        history: List<Tracer.Method?>,
        returnedObject: Any?,
    ): TraceHistoryManagementAction

    override fun trace(
        defaultMessage: String,
        java: Boolean,
        annotationName: String,
        method: Tracer.Method,
        history: List<Tracer.Method?>,
        parameterValues: Array<Any?>,
    ): TraceHistoryManagementAction {
        if (parameterValues.size != 2) {
            throw Error("tracking error")
        }
        val returnedObject = parameterValues[0]
        val callingMethod =
            history.getMethodFromHistory(parameterValues[1] as String)?.originalName
                ?: parameterValues[1] as String
        return traceReturn(defaultMessage, annotationName, callingMethod, history, returnedObject)
    }

    private fun List<Tracer.Method?>.getMethodFromHistory(methodObf: String): Tracer.Method? {
        return this.find { it?.possiblyObfuscatedMethod == methodObf }
    }
}

/**
 * part of the process to trace return value.
 * Implement this
 */
abstract class Returner {

    fun <T> trace(returnValue: T): T {
        val stackElement = Throwable().stackTrace.run {
            if (size >= 3) {
                this[2]
            } else {
                null
            }
        }
        val fullMethodName =
            (stackElement?.className ?: "") + "." + (stackElement?.methodName ?: "")
        return onAnnotateThisToTrace(returnValue, fullMethodName)
    }

    abstract fun <T> onAnnotateThisToTrace(toTrace: T, callingMethod: String): T
}
/*

fun <T> Any?.withTrace(): T {
    return ExampleReturner().trace(this as T)
}

class ExampleReturner() : Returner() {
    @ReturnTrace
    override fun <T> onAnnotateThisToTrace(toTrace: T, callingMethod: String): T {
        return toTrace
    }
}

 */
