package com.ybo.trackingplugin.tasks.utils.impl.patterns.producers

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.utils.PatternProducer
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinMethodPatternName
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinParamHigherOrderFunctionPattern
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinParamPattern
import com.ybo.trackingplugin.tasks.utils.impl.patterns.KotlinParamPatternName

class KotlinParamPatternProducer(
    private val methodType: KotlinMethodPatternName,
) : PatternProducer<KotlinParamPatternName> {
    override fun produce(): List<PatternToSearch<KotlinParamPatternName>> {
        return when (methodType) {
            KotlinMethodPatternName.KotlinHigherOrderFunctionWithNoParams,
            KotlinMethodPatternName.KotlinHigherOrderFunctionWithParams,
            -> listOf(
                KotlinParamHigherOrderFunctionPattern(),
            )

            KotlinMethodPatternName.KotlinExtensionFunction,
            KotlinMethodPatternName.KotlinNormalMethod,
            -> listOf(
                KotlinParamPattern(),
            )
        }
    }
}
