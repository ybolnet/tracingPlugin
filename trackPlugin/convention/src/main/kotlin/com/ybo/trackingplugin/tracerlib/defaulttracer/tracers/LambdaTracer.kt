package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

import com.ybo.trackingplugin.tracerlib.Tracer

abstract class LambdaTracer : ContextualTracer() {

    /**
     * override this to be warned of a traced lambda being called,
     * giving you the opportunity to trace it
     */
    abstract fun traceLambdaCall(
        defaultMessage: String,
        contextParentMethod: ReturningContext,
        callingContext: ReturningContext,
        history: List<Tracer.Method?>,
        params: List<Any?>,
    ): Tracer.TraceHistoryManagementAction

    override fun trace(
        java: Boolean,
        method: Tracer.Method,
        history: List<Tracer.Method?>,
        parameterValues: Array<Any?>,
    ): Tracer.TraceHistoryManagementAction {
        if (parameterValues.size != 4) {
            throw Error("tracking error")
        }
        val returnedObject = parameterValues[0]
        val contextMethodName = parameterValues[1] as String
        val callingMethodName = parameterValues[2] as String
        val parameters = parameterValues[3] as List<Any?>
        val contextOfReturn = history.getMethodFromHistory(contextMethodName)?.let {
            ReturningContext.CompleteMethod(it)
        } ?: ReturningContext.MethodName(contextMethodName)
        val calling = history.getMethodFromHistory(callingMethodName)?.let {
            ReturningContext.CompleteMethod(it)
        } ?: ReturningContext.MethodName(callingMethodName)
        val defaultMessage =
            "in context ${contextOfReturn.asLoggable()}, in method ${calling.asLoggable()} , $returnedObject ($parameters) WAS CALLED" +
                " ${contextOfReturn.getAnnotation()?.asTag()} ${Tracer.TAG}"
        return traceLambdaCall(defaultMessage, contextOfReturn, calling, history, parameters)
    }
}

/**
 * default factory for lambda calls when user does not wish to customize
 */
class DefaultLambdaTracerFactory : Tracer.Factory {
    override fun create(): Tracer {
        return object : LambdaTracer() {
            override fun traceLambdaCall(
                defaultMessage: String,
                contextParentMethod: ReturningContext,
                callingContext: ReturningContext,
                history: List<Tracer.Method?>,
                params: List<Any?>,
            ): Tracer.TraceHistoryManagementAction {
                println(defaultMessage)
                return Tracer.TraceHistoryManagementAction.None
            }
        }
    }
}
