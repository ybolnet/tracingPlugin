package com.ybo.trackingplugin.tasks.utils.impl.patterns

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark

internal class JavaMethodPattern(
    val markToLookFor: TraceAnnotationMark,
) : PatternToSearch<JavaMethodPatternName> {
    private val toProcessAnnotationShort: String = markToLookFor.shortVersion
    private val toProcessAnnotationLong: String = markToLookFor.longVersion
    override val name = JavaMethodPatternName.JavaNormalMethod
    override fun regex(): Regex {
        return Regex("($toProcessAnnotationShort|$toProcessAnnotationLong)\\s*\\n*(?:\\s*@[^\\n]*\\s*\\n*)*\\s*(?:public\\s+|protected\\s+|private\\s+|static\\s+|\\s)[\\w<>\\[\\]\\.]+\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{[ \\t]*\\n*([ \\t]*)")
    }
}