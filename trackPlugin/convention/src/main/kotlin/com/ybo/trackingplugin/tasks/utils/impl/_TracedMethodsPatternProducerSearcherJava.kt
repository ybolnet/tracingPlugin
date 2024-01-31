package com.ybo.trackingplugin.tasks.utils.impl

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.data.TracedLanguage
import com.ybo.trackingplugin.tasks.data.TracedMethod

class _TracedMethodsPatternProducerSearcherJava(
    private val markToLookFor: TraceAnnotationMark,
) : _PatternProducerSearcher<TracedMethod>() {

    override fun produce(): List<PatternToSearch> {
        return listOf(
            NormalMethod(markToLookFor),
        )
    }

    override fun createSearchedResult(matchResult: MatchResult, patternName: String): TracedMethod {
        return TracedMethod(
            wholeMethod = matchResult.groupValues[0],
            indentationInsideMethod = if (matchResult.groupValues.size > 4) matchResult.groupValues[4] else " ",
            paramBlock = matchResult.groupValues[3],
            methodName = matchResult.groupValues[2],
        )
    }

    private class NormalMethod(
        val markToLookFor: TraceAnnotationMark,
    ) : PatternToSearch {
        private val toProcessAnnotationShort: String = markToLookFor.shortVersion
        private val toProcessAnnotationLong: String = markToLookFor.longVersion
        override val name = "JavaNormalMethod"
        override fun regex(): Regex {
            return Regex("($toProcessAnnotationShort|$toProcessAnnotationLong)\\s*\\n*(?:\\s*@[^\\n]*\\s*\\n*)*\\s*(?:public\\s+|protected\\s+|private\\s+|static\\s+|\\s)[\\w<>\\[\\]\\.]+\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{[ \\t]*\\n*([ \\t]*)")
        }
    }
    /*
        private fun markedNormalMethods(): PatternToSearch {
            val toProcessAnnotationShort = markToLookFor.shortVersion
            val toProcessAnnotationLong = markToLookFor.longVersion
            return PatternToSearch(
                name = "JavaNormalMethod",
                language = markToLookFor.language,
                regex = Regex("($toProcessAnnotationShort|$toProcessAnnotationLong)\\s*\\n*(?:\\s*@[^\\n]*\\s*\\n*)*\\s*(?:public\\s+|protected\\s+|private\\s+|static\\s+|\\s)[\\w<>\\[\\]\\.]+\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{[ \\t]*\\n*([ \\t]*)"),
            )
        }*/
}
