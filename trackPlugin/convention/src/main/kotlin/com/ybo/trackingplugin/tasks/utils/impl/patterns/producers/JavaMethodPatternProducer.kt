package com.ybo.trackingplugin.tasks.utils.impl.patterns.producers

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.utils.PatternProducer
import com.ybo.trackingplugin.tasks.utils.impl.patterns.JavaMethodPattern
import com.ybo.trackingplugin.tasks.utils.impl.patterns.JavaMethodPatternName

class JavaMethodPatternProducer(
    private val markToLookFor: TraceAnnotationMark,
) : PatternProducer<JavaMethodPatternName> {
    override fun produce(): List<PatternToSearch<JavaMethodPatternName>> {
        return listOf(
            JavaMethodPattern(markToLookFor),
        )
    }
}
