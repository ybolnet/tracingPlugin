package com.ybo.trackingplugin.tracerlib.defaulttracer.tracers

import org.gradle.api.GradleException

/**
 * part of the process to trace lambda value.
 * Implement this
 */
abstract class LambdaReturner {

    fun <T : Function<*>> trace(lambda: T): T {
        val stacktrace = Throwable().stackTrace
        val stackElement = stacktrace.run {
            if (size >= 3) {
                this[2]
            } else {
                null
            }
        }

        val fullMethodName =
            (stackElement?.className ?: "") + "." + (stackElement?.methodName ?: "")
        return lambda.decorate(
            lambdaReturner = this,
            contextMethod = fullMethodName,
        )
    }

    private fun onInvoke(
        decorated: Function<*>,
        contextMethod: String,
        params: List<Any?>,
    ) {
        val stack = Throwable().stackTrace
        var indexContext = 0
        var candidateIndex = 0
        run breaking@{
            stack.forEach {
                println("stack2" + it.methodName + "vs $contextMethod")
                if (it.methodName == contextMethod.lastPart()) {
                    indexContext = candidateIndex
                    return@breaking
                }
                candidateIndex++
            }
        }

        println("index context: $indexContext")
        val callingMethodStackElement = stack[(indexContext - 1).coerceAtLeast(0)]
        val contextMethodStackElement = stack[(indexContext).coerceAtLeast(0)]

        val callingMethod =
            (
                callingMethodStackElement?.className
                    ?: ""
                ) + "." + (callingMethodStackElement?.methodName ?: "")
        val contextMethod1 = (
            contextMethodStackElement?.className
                ?: ""
            ) + "." + (contextMethodStackElement?.methodName ?: "")
        println("calling: $callingMethod context $contextMethod1 actual context $contextMethod")
        onAnnotateThisToTrace(decorated, contextMethod, callingMethod, params)
    }

    abstract fun <T> onAnnotateThisToTrace(
        toTrace: T,
        contextMethod: String,
        callingMethod: String,
        params: List<Any?>,
    ): T

    private fun <T : Function<*>> T.decorate(
        lambdaReturner: LambdaReturner,
        contextMethod: String,
    ): T {
        println("decorator: this is $this")
        return when (this) {
            is Function0<*> -> {
                println("function with 0 args")
                TracedFunction0Decorator(
                    this,
                    lambdaReturner,
                    contextMethod,
                ) as T
            }

            is Function1<*, *> -> {
                println("function with 1 args")
                TracedFunction1Decorator(
                    this,
                    lambdaReturner,
                    contextMethod,
                ) as T
            }

            is Function2<*, *, *> ->
                TracedFunction2Decorator(
                    this,
                    lambdaReturner,
                    contextMethod,
                ) as T

            is Function3<*, *, *, *> ->
                TracedFunction3Decorator(
                    this,
                    lambdaReturner,
                    contextMethod,
                ) as T

            else -> throw GradleException("the lambda has too many arguments to be traced")
        }
    }

    private class TracedFunction0Decorator<R>(
        private val decorated: Function0<R>,
        private val lambdaReturner: LambdaReturner,
        private val contextMethod: String,
    ) : Function0<R> {
        override fun invoke(): R {
            lambdaReturner.onInvoke(decorated, contextMethod, emptyList())
            return decorated.invoke()
        }
    }

    private class TracedFunction1Decorator<R, P1>(
        private val decorated: Function1<P1, R>,
        private val lambdaReturner: LambdaReturner,
        private val contextMethod: String,
    ) : Function1<P1, R> {
        override fun invoke(p1: P1): R {
            lambdaReturner.onInvoke(
                decorated,
                contextMethod,
                listOf(p1 as Any),
            )
            return decorated.invoke(p1)
        }
    }

    private class TracedFunction2Decorator<R, P1, P2>(
        private val decorated: Function2<P1, P2, R>,
        private val lambdaReturner: LambdaReturner,
        private val contextMethod: String,
    ) : Function2<P1, P2, R> {
        override fun invoke(p1: P1, p2: P2): R {
            lambdaReturner.onInvoke(
                decorated,
                contextMethod,
                listOf(p1 as Any, p2 as Any),
            )
            return decorated.invoke(p1, p2)
        }
    }

    private class TracedFunction3Decorator<R, P1, P2, P3>(
        private val decorated: Function3<P1, P2, P3, R>,
        private val lambdaReturner: LambdaReturner,
        private val contextMethod: String,
    ) : Function3<P1, P2, P3, R> {
        override fun invoke(p1: P1, p2: P2, p3: P3): R {
            lambdaReturner.onInvoke(
                decorated,
                contextMethod,
                listOf(
                    p1 as Any,
                    p2 as Any,
                    p3 as Any,
                ),
            )
            return decorated.invoke(p1, p2, p3)
        }
    }

    private fun String.lastPart(): String {
        val lastDotIndex = lastIndexOf('.')
        return if (lastDotIndex == -1) {
            this // If no dot found, return the original string
        } else {
            substring(lastDotIndex + 1)
        }
    }
}
