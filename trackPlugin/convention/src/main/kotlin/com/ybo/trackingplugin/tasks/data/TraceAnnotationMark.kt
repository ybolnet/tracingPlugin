package com.ybo.trackingplugin.tasks.data

/** represents a annotation that user put on a method */
class TraceAnnotationMark(
    private val wholeClass: String?,
    val language: TracedLanguage,
    val shortVersion: String,
    val longVersion: String,
) {
    val importStatement: String
        get() = ("import $wholeClass") + when (language) {
            TracedLanguage.JAVA -> ";"
            TracedLanguage.KOTLIN -> ""
            TracedLanguage.OTHER -> ""
        }

    override fun toString(): String {
        return "<$shortVersion language $language>"
    }
}
