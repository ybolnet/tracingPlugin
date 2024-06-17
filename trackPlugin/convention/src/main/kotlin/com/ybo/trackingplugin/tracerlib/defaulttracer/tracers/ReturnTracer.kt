package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

import com.ybo.trackingplugin.tracerlib.Tracer
import com.ybo.trackingplugin.tracerlib.Tracer.TraceHistoryManagementAction

/**
 * tracer specialized for the return value of methods.
 * in the config{} closure of the plugin, use
 *
 *          addReturnValueConfig {
 *             tracerFactory = "OPTIONALY HERE PUT FACTORY CREATING A SUBTYPE OF ReturnValueTracer"
 *         }
 */
abstract class ReturnValueTracer() : ContextualTracer() {

    /**
     * override this to be warned of a method returning,
     * giving you the opportunity to trace it
     */
    abstract fun traceReturn(
        defaultMessage: String,
        methodReturning: ReturningContext,
        history: List<Tracer.Method?>,
        returnedObject: Any?,
    ): TraceHistoryManagementAction

    override fun trace(
        java: Boolean,
        method: Tracer.Method,
        history: List<Tracer.Method?>,
        parameterValues: Array<Any?>,
    ): TraceHistoryManagementAction {
        if (parameterValues.size != 2) {
            throw Error("tracking error")
        }
        val returnedObject = parameterValues[0]
        val contextMethodName = parameterValues[1] as String
        val contextOfReturn = history.getMethodFromHistory(contextMethodName)?.let {
            ReturningContext.CompleteMethod(it)
        } ?: ReturningContext.MethodName(contextMethodName)
        val defaultMessage = "<-- ${contextOfReturn.asLoggable()}(...) RETURNING $returnedObject" +
            " ${contextOfReturn.getAnnotation()?.asTag()} ${Tracer.TAG}"
        return traceReturn(defaultMessage, contextOfReturn, history, returnedObject)
    }
}

/**
 * default factory for return values when user does not wish to customize
 */
class DefaultReturnValueTracerFactory : Tracer.Factory {
    override fun create(): Tracer {
        return object : ReturnValueTracer() {
            override fun traceReturn(
                defaultMessage: String,
                methodReturning: ReturningContext,
                history: List<Tracer.Method?>,
                returnedObject: Any?,
            ): TraceHistoryManagementAction {
                println(defaultMessage)
                return TraceHistoryManagementAction.None
            }
        }
    }
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
