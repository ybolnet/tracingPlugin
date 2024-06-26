package com.ybo.trackingplugin.tracerlib

import com.ybo.trackingplugin.decodedFromB64

object TracePerformer {

    fun trace(
        tracerFactory: Tracer.Factory,
        method: String,
        java: Boolean,
        annotationName: String,
        paramz: Array<Any?>,
        line: Int,
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
        val methodNameToTheBestOfKnowledge = if (method.isNotBlank()) {
            method.decodedFromB64()
        } else {
            stackElement?.methodName ?: ""
        }
        val fullMethodName = (stackElement?.className ?: "") +
            "." + methodNameToTheBestOfKnowledge
        val fullMethodNamePossiblyObfuscated = (
            (stackElement?.className ?: "") +
                "." + stackElement?.methodName
            )

        val stringLink = stackElement?.fileName?.let {
            "$it:$line"
        } ?: ""

        val annotation = Tracer.TraceAnnotationName(annotationName.decodedFromB64())

        val currentMethod = Tracer.Method(
            originalName = fullMethodName,
            possiblyObfuscatedMethod = fullMethodNamePossiblyObfuscated,
            link = stringLink,
            annotation = annotation,
        )

        historyOfMethods.add(currentMethod)
        tracer.trace(
            java = java,
            method = currentMethod,
            history = historyOfMethods,
            parameterValues = paramz,
        ).also {
            when (it) {
                Tracer.TraceHistoryManagementAction.Clear -> historyOfMethods.clear()
                Tracer.TraceHistoryManagementAction.NullOldest -> {
                    if (historyOfMethods.size >= 1) {
                        historyOfMethods[0] = null
                    }
                }

                Tracer.TraceHistoryManagementAction.None -> {}
                is Tracer.TraceHistoryManagementAction.NullAtIndex -> {
                    if (it.index < historyOfMethods.size) {
                        historyOfMethods[it.index] = null
                    }
                }
            }
        }
    }

    private val historyOfMethods: MutableList<Tracer.Method?> = mutableListOf()
}
