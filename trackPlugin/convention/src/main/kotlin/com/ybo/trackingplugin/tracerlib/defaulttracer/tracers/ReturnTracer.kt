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
        methodReturning: ReturningContext,
        history: List<Tracer.Method?>,
        returnedObject: Any?,
    ): TraceHistoryManagementAction

    override fun trace(
        defaultMessage: String,
        java: Boolean,
        method: Tracer.Method,
        history: List<Tracer.Method?>,
        parameterValues: Array<Any?>,
    ): TraceHistoryManagementAction {
        if (parameterValues.size != 2) {
            throw Error("tracking error")
        }
        val returnedObject = parameterValues[0]
        val contextOfReturn = history.getMethodFromHistory(parameterValues[1] as String)?.let {
            ReturningContext.CompleteMethod(it)
        } ?: ReturningContext.MethodName(parameterValues[1] as String)
        return traceReturn(defaultMessage, contextOfReturn, history, returnedObject)
    }

    /**
     * represents the code region (a method) returning the value.
     * is the history is handled well, it is a [CompleteMethod].
     * But if in the previous [Tracer.trace] calls you returned a value to manipulate
     * the trace history, the method might have disappeared from history,
     * and in this case, only the (possibly obfuscated) name of the returning function has
     * survived. In this case we are dealing with a [MethodName]
     */
    sealed interface ReturningContext {
        @JvmInline
        value class CompleteMethod(val value: Tracer.Method) : ReturningContext

        @JvmInline
        value class MethodName(val value: String) : ReturningContext
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
