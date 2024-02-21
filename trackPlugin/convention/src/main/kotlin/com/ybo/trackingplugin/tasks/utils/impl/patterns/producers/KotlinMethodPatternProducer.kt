package com.ybo.trackingplugin.tasks.utils.impl.patterns.producers

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.utils.PatternProducer
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodExtensionFunctionPattern
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodHigherOrderFunctionNoParamsPattern
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodHigherOrderFunctionPattern
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodPattern
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodPatternName

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
    }
}
