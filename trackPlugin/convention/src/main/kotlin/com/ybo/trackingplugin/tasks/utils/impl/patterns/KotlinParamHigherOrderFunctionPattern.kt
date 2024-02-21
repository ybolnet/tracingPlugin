package com.ybo.trackingplugin.tasks.utils.impl.patterns

import com.ybo.trackingplugin.tasks.data.PatternToSearch

class KotlinParamHigherOrderFunctionPattern : PatternToSearch<KotlinParamPatternName> {
    override val name: KotlinParamPatternName = KotlinParamPatternName.KotlinHigherOrderParam

    override fun regex(): Regex {
        return Regex("\\b(\\w+)\\s*(?::\\s*[^,=]\\s*)?,?\\s*")
    }
}
