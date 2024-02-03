package com.ybo.trackingplugin.tracerlib

object TracePerformer {

    fun trace(
        tracerFactory: Tracer.Factory,
        method: String,
        java: Boolean,
        paramz: Array<Any?>,
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
        val fullMethodName = (stackElement?.className ?: "") + "." + method
        tracer.trace(
            defaultMessage = makeMessage(java, fullMethodName, paramz),
            java = java,
            methodName = fullMethodName,
            parameterValues = paramz,
        )
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
}
