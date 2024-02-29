package com.ybo.trackingplugin.tasks.utils.impl.patterns.producers

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.utils.PatternProducer
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodExtensionFunctionPattern
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodHigherOrderFunctionNoParamsPattern
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodHigherOrderFunctionPattern
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodPattern
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodPatternDecorator
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodPatternName
import java.lang.IllegalStateException

class KotlinMethodPatternProducer(
    private val markToLookFor: TraceAnnotationMark,
) : PatternProducer<KotlinMethodPatternName> {
    override fun produce(): List<PatternToSearch<KotlinMethodPatternName>> {
        return listOf(
            KotlinMethodPattern(markToLookFor),
            KotlinMethodExtensionFunctionPattern(markToLookFor),
            KotlinMethodHigherOrderFunctionPattern(markToLookFor),
            KotlinMethodHigherOrderFunctionNoParamsPattern(markToLookFor),
        )
            .orThrowIfIncoherent()
            .also {
                println("produced : $it")
            }
    }

    private fun kotlinMethodFixForComplexParams(extensionFunction: Boolean): KotlinMethodPattern {
        return KotlinMethodPatternDecorator(
            substrate = if (extensionFunction) {
                KotlinMethodPattern(markToLookFor)
            } else {
                KotlinMethodExtensionFunctionPattern(markToLookFor)
            },
            paramsWithCaptureReplacement = "\\((\\s*(?:\\w*\\s*:\\s*[\\w\\.]*(?:\\(.*\\)\\s*->\\s*[\\w\\.]*)?(?:\\s*=\\s*.*)?,?\\s*)*\\s*)\\)",
        )
    }

    private fun List<PatternToSearch<KotlinMethodPatternName>>.orThrowIfIncoherent(): List<PatternToSearch<KotlinMethodPatternName>> {
        val mapOfPatternName = mutableMapOf<KotlinMethodPatternName, Boolean>()
        for (pattern in this) {
            if (mapOfPatternName.containsKey(pattern.name)) {
                throw IllegalStateException("produced pattern list should not contain two element of same name")
            }
            mapOfPatternName[pattern.name] = true
        }
        return this
    }
}
