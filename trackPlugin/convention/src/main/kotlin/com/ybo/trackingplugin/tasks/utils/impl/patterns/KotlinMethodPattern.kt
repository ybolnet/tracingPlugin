package com.ybo.trackingplugin.tasks.utils.impl.patterns

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark

/**
 * methods like:
 * fun methodName( maybeParams ) : MaybeType { HERE_MANDATORY_NEW_LINE
 * */
internal open class KotlinMethodPattern(
    open val markToLookFor: TraceAnnotationMark,
) : PatternToSearch<KotlinMethodPatternName> {

    override val name: KotlinMethodPatternName = KotlinMethodPatternName.KotlinNormalMethod
    open val fillerAnnotations: String =
        "(?:\\s*@[\\w\\.]*(?:\\(.*\\))?\\s*)" // "(?:\\s*@[^\\n]*\\s*\\n*)"
    open val preFunKeywords: String =
        "(?:override\\s+|inline\\s+|private\\s+|public\\s+|protected\\s+|suspend\\s+)"
    open val funKeyword: String = "fun"
    open val generics: String = "(?:\\s*<\\s*$VAR_NAME\\s*$RETURNED_TYPE?>\\s*)"
    open val methodName: String = "\\w+"
    open val paramsWithCapture: String =
        "\\((\\s*(?:\\w*\\s*:\\s*[\\w\\.\\?]*(?:\\(.*\\)\\s*->\\s*[\\w\\.]*)?(?:\\s*=\\s*.*)?,?\\s*)*\\s*)\\)"

    // "\\(([^)]*)\\)"
    open val returnType: String = "(?::\\s*$ANY_TYPE)"
    open val insideIndentation: String = "\\s*"

    open fun getTraceAnnotation(): String {
        val shortVersion = markToLookFor.shortVersion
        val longVersion = markToLookFor.longVersion.replace(".", "\\.")
        return "$shortVersion|$longVersion"
    }

    override fun regex(): Regex {
        return Regex(
            "(${getTraceAnnotation()})\\s*\\n*" +
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

    companion object {
        const val ANY_TYPE = "[\\w\\.<>\\? ]+"
        const val VAR_NAME = "\\w*"
        const val RETURNED_TYPE = "(?:\\s*:\\s*$ANY_TYPE)"
    }
}
