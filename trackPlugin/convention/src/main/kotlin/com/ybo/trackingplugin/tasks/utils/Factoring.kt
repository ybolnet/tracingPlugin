package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.BrowsingTask
import com.ybo.trackingplugin.tasks.ProcessTraceTask
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.data.TracedLanguage
import com.ybo.trackingplugin.tasks.data.TracedMethod
import com.ybo.trackingplugin.tasks.data.TracedMethodParam
import com.ybo.trackingplugin.tasks.utils.impl._CodeGeneratorJava
import com.ybo.trackingplugin.tasks.utils.impl._CodeGeneratorKotlin
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodPatternName
import com.ybo.trackingplugin.tasks.utils.impl.patterns.MethodPatternNames
import com.ybo.trackingplugin.tasks.utils.impl.patterns.ParamsPatternName
import com.ybo.trackingplugin.tasks.utils.impl.patterns.producers.JavaMethodPatternProducer
import com.ybo.trackingplugin.tasks.utils.impl.patterns.producers.JavaParamPatternProducer
import com.ybo.trackingplugin.tasks.utils.impl.patterns.producers.KotlinMethodPatternProducer
import com.ybo.trackingplugin.tasks.utils.impl.patterns.producers.KotlinParamPatternProducer
import com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.BasePatternSearcher
import com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.repetitionHandler.TraceMethodHitDistinguisher
import com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers.KotlinAndJavaMethodNormalResolver
import com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers.KotlinAndJavaMethodResolver
import com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers.KotlinAndJavaParamResolver
import com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers.KotlinMethodHigherOrderNoParamsResolver
import com.ybo.trackingplugin.tasks.utils.impl.patterns.searchers.resolvers.KotlinMethodHigherOrderResolver
import com.ybo.trackingplugin.tasks.utils.impl.patterns.sorters.MethodsSorter
import com.ybo.trackingplugin.toB64
import org.gradle.api.GradleException

fun createCodeGenerator(language: TracedLanguage): CodeGenerator {
    return when (language) {
        TracedLanguage.JAVA -> _CodeGeneratorJava()
        TracedLanguage.KOTLIN -> _CodeGeneratorKotlin()
        TracedLanguage.OTHER -> throw GradleException("unknown language")
    }
}

fun createPatternProducerForTracedMethods(markToLookFor: TraceAnnotationMark):
    PatternProducer<MethodPatternNames> {
    return when (markToLookFor.language) {
        TracedLanguage.JAVA -> JavaMethodPatternProducer(markToLookFor)
        TracedLanguage.KOTLIN -> KotlinMethodPatternProducer(markToLookFor)
        TracedLanguage.OTHER -> throw GradleException("unknown language")
    }
}

fun createPatternSearcherForTracedMethods(
    markToLookFor: TraceAnnotationMark,
): PatternSearcher<TracedMethod, MethodPatternNames> {
    return when (markToLookFor.language) {
        TracedLanguage.KOTLIN, TracedLanguage.JAVA,
        -> BasePatternSearcher(
            resolver = KotlinAndJavaMethodResolver(
                normalResolver = KotlinAndJavaMethodNormalResolver(),
                higherOrderResolver = KotlinMethodHigherOrderResolver(),
                higherOrderNoParamsResolver = KotlinMethodHigherOrderNoParamsResolver(),
            ),
            hitDistinguisher = TraceMethodHitDistinguisher(),
        )

        TracedLanguage.OTHER -> throw GradleException("unknown language")
    }
}

fun createPatternProducerForTracedParams(
    language: TracedLanguage,
    methodPatternType: MethodPatternNames,
): PatternProducer<ParamsPatternName> {
    return when (language) {
        TracedLanguage.JAVA -> JavaParamPatternProducer()
        TracedLanguage.KOTLIN -> if (methodPatternType is KotlinMethodPatternName) {
            KotlinParamPatternProducer(methodPatternType)
        } else {
            throw GradleException("trying to produce kotlin params patterns for a non kotlin mehthod")
        }

        TracedLanguage.OTHER -> throw GradleException("unknown language")
    }
}

internal fun createSignalProcessor(): ProcessTraceTask.SignalProcessor = SignalProcessorImpl(
    createPatternProducerForMethods = ::createPatternProducerForTracedMethods,
    createPatternSearcherForMethods = ::createPatternSearcherForTracedMethods,
    createMethodSorter = { text -> MethodsSorter(text) },
    createPatternProducerForParams = ::createPatternProducerForTracedParams,
    createPatternSearcherForParams = ::createPatternSearcherForTracedParams,
)

internal fun createPatternSearcherForTracedParams(
    language: TracedLanguage,
    methodPatternType: MethodPatternNames,
): PatternSearcher<TracedMethodParam, ParamsPatternName> {
    return when (language) {
        TracedLanguage.KOTLIN, TracedLanguage.JAVA,
        -> BasePatternSearcher(KotlinAndJavaParamResolver())

        TracedLanguage.OTHER -> throw GradleException("unknown language")
    }
}

internal fun createSignalsExtractor(): BrowsingTask.SignalsExtractor = SignalsExtractorImpl()

internal fun createReverseTraceAnnotationConfig(traceAnnotationConfig: String): String {
    return "com.ybo.trackingplugin.tracerlib.defaulttracer.ReverseTrace(target = \"${traceAnnotationConfig.toB64()}\")"
}
