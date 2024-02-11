package com.ybo.trackingplugin.tracerlib

import com.ybo.trackingplugin.decodedFromB64

object TracePerformer {

    fun trace(
        tracerFactory: Tracer.Factory,
        method: String,
        java: Boolean,
        paramz: Array<Any?>,
        alterationOffset: Int,
    ) {
        val tracer = tracerFactory.create()
        val throwable = Throwable()
        val stackElement = throwable.stackTrace.run {
            if (size >= 2) {
                this[1]
            } else {
                null
            }
        }
        val fullMethodName = (stackElement?.className ?: "") +
            "." + method.decodedFromB64()
        val fullMethodNamePossiblyObfuscated = (
            (stackElement?.className ?: "") +
                "." + stackElement?.methodName
            )
        val line = stackElement?.lineNumber?.let {
            (it - alterationOffset).coerceAtLeast(0)
        } ?: 0

        val stringLink = stackElement?.fileName?.let {
            "$it:$line"
        } ?: ""

        val currentMethod = Tracer.Method(
            originalName = fullMethodName,
            possiblyObfuscatedMethod = fullMethodNamePossiblyObfuscated,
            link = stringLink,
        )
        historyOfMethods.add(currentMethod)
        val invalidateHistory = tracer.trace(
            defaultMessage = makeMessage(java, fullMethodName, paramz),
            java = java,
            method = currentMethod,
            history = historyOfMethods,
            parameterValues = paramz,
        )
        if (invalidateHistory) {
            historyOfMethods.clear()
        }
    }

    fun makeMessage(
        java: Boolean,
        methodName: String,
        parameterValues: Array<Any?>,
    ): String {
        val params = parameterValues.joinToString(", ") { param ->
            param?.let {
                it.toString() + " : " + it.javaClass.name
            } ?: "null"
        }

        return " $methodName($params) "
    }

    private val historyOfMethods: MutableList<Tracer.Method> = mutableListOf()
}
