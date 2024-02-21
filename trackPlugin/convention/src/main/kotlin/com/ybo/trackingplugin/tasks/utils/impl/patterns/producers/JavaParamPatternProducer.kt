package com.ybo.trackingplugin.tasks.utils.impl.patterns.producers

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.utils.PatternProducer
import com.ybo.trackingplugin.tasks.utils.impl.patterns.JavaParamPatternName
import com.ybo.trackingplugin.tasks.utils.impl.patterns.JavaParamsPattern

class JavaParamPatternProducer() : PatternProducer<JavaParamPatternName> {
    override fun produce(): List<PatternToSearch<JavaParamPatternName>> {
        return listOf(JavaParamsPattern())
    }
}
