package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

import com.ybo.trackingplugin.tracerlib.Tracer

/**
 * base tracer used when you trace something in the body on a function (for instance a return value, or a lambda call)
 * descendants of this tracer have easy method to access parent function annotation and name
 */
abstract class ContextualTracer : Tracer {

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

    protected fun ReturningContext.asLoggable(): String {
        return when (this) {
            is ReturningContext.CompleteMethod -> value.asLoggable()
            is ReturningContext.MethodName -> "NOLINK - ${value.lastPart()}"
        }
    }

    protected fun ReturningContext.getAnnotation(): Tracer.TraceAnnotationName? {
        return when (this) {
            is ReturningContext.CompleteMethod -> value.annotation
            is ReturningContext.MethodName -> null
        }
    }

    protected fun List<Tracer.Method?>.getMethodFromHistory(methodObf: String): Tracer.Method? {
        return this.find { it?.possiblyObfuscatedMethod == methodObf }
    }
}
