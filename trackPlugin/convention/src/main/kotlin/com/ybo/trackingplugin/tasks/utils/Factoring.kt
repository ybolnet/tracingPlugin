package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.data.TracedLanguage
import com.ybo.trackingplugin.tasks.data.TracedMethod
import com.ybo.trackingplugin.tasks.data.TracedMethodParam
import com.ybo.trackingplugin.tasks.utils.impl._CodeGeneratorJava
import com.ybo.trackingplugin.tasks.utils.impl._CodeGeneratorKotlin
import com.ybo.trackingplugin.tasks.utils.impl._TracedMethodsPatternProducerSearcherJava
import com.ybo.trackingplugin.tasks.utils.impl._TracedMethodsPatternProducerSearcherKotlin
import com.ybo.trackingplugin.tasks.utils.impl._TracedParamsPatternProducerSearcherJava
import com.ybo.trackingplugin.tasks.utils.impl._TracedParamsPatternProducerSearcherKotlin
import org.gradle.api.GradleException

fun createCodeGenerator(language: TracedLanguage): CodeGenerator {
    return when (language) {
        TracedLanguage.JAVA -> _CodeGeneratorJava()
        TracedLanguage.KOTLIN -> _CodeGeneratorKotlin()
        TracedLanguage.OTHER -> throw GradleException("unknown language")
    }
}

fun createPatternProducerForTracedMethods(markToLookFor: TraceAnnotationMark): PatternProducer {
    return when (markToLookFor.language) {
        TracedLanguage.JAVA -> _TracedMethodsPatternProducerSearcherJava(markToLookFor)
        TracedLanguage.KOTLIN -> _TracedMethodsPatternProducerSearcherKotlin(markToLookFor)
        TracedLanguage.OTHER -> throw GradleException("unknown language")
    }
}

fun createPatternSearcherForTracedMethods(
    markToLookFor: TraceAnnotationMark,
): PatternSearcher<TracedMethod> {
    return when (markToLookFor.language) {
        TracedLanguage.JAVA -> _TracedMethodsPatternProducerSearcherJava(markToLookFor)
        TracedLanguage.KOTLIN -> _TracedMethodsPatternProducerSearcherKotlin(markToLookFor)
        TracedLanguage.OTHER -> throw GradleException("unknown language")
    }
}

fun createPatternProducerForTracedParams(language: TracedLanguage): PatternProducer {
    return when (language) {
        TracedLanguage.JAVA -> _TracedParamsPatternProducerSearcherJava()
        TracedLanguage.KOTLIN -> _TracedParamsPatternProducerSearcherKotlin()
        TracedLanguage.OTHER -> throw GradleException("unknown language")
    }
}

fun createPatternSearcherForTracedParams(language: TracedLanguage): PatternSearcher<TracedMethodParam> {
    return when (language) {
        TracedLanguage.JAVA -> _TracedParamsPatternProducerSearcherJava()
        TracedLanguage.KOTLIN -> _TracedParamsPatternProducerSearcherKotlin()
        TracedLanguage.OTHER -> throw GradleException("unknown language")
    }
}
