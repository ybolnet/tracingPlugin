package com.ybo.trackingplugin.tasks.utils.impl

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.data.TracedMethod

class _TracedMethodsPatternProducerSearcherKotlin(
    private val markToLookFor: TraceAnnotationMark,
) : _PatternProducerSearcher<TracedMethod>() {

    override fun createSearchedResult(matchResult: MatchResult, patternName: String): TracedMethod {
        return TracedMethod(
            wholeMethod = matchResult.groupValues[0],
            indentationInsideMethod = if (matchResult.groupValues.size > 4) matchResult.groupValues[4] else " ",
            paramBlock = matchResult.groupValues[3],
            methodName = matchResult.groupValues[2],
        )
    }

    override fun produce(): List<PatternToSearch> {
        return listOf(
            NormalMethod(markToLookFor),
            ExtensionMethod(markToLookFor),
        )
    }

    private open class NormalMethod(
        open val markToLookFor: TraceAnnotationMark,
    ) : PatternToSearch {

        override val name = "KotlinNormalMethod"
        open val fillerAnnotations: String = "(?:\\s*@[^\\n]*\\s*\\n*)"
        open val preFunKeywords: String = "(?:override\\s+|inline\\s+|private\\s+|public\\s+)"
        open val funKeyword: String = "fun"
        open val generics: String = "(?:\\s*<\\s*\\w*\\s*>\\s*)"
        open val methodName: String = "\\w+"
        open val paramsWithCapture: String = "\\(([^)]*)\\)"
        open val returnType: String = "(?::\\s*\\w+)"
        open val insideIndentation: String = "\\s*"

        override fun regex(): Regex {
            val traceAnnotation = "${markToLookFor.shortVersion}|${markToLookFor.longVersion}"
            return Regex(
                "($traceAnnotation)\\s*\\n*" +
                    "$fillerAnnotations*\\s*" +
                    "$preFunKeywords*" +
                    "$funKeyword\\b" +
                    "$generics?\\s+" +
                    "($methodName)\\s*" +
                    "$paramsWithCapture\\s*" +
                    "$returnType?\\s*\\{[\\t ]*\\n" +
                    "($insideIndentation)",
            )
        }

        override fun toString(): String {
            return "{name $name, regex: " + regex() + "}"
        }
    }

    private class ExtensionMethod(
        override val markToLookFor: TraceAnnotationMark,
    ) : NormalMethod(markToLookFor) {
        override val name = "KotlinExtensionMethod"
        override val methodName: String = "[\\w\\.]+"
    }
}
