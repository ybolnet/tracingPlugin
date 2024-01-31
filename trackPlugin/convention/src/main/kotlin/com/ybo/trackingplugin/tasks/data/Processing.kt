package com.ybo.trackingplugin.tasks.data

import java.io.File

data class TracedMethod(
    val wholeMethod: String,
    val indentationInsideMethod: String,
    val paramBlock: String,
    val methodName: String = "",
)

data class TracedMethodParam(
    val name: String,
)

enum class TracedLanguage(val fileExtension: String) {
    JAVA(".java"),
    KOTLIN(".kt"),
    OTHER(""),
}

fun File.getLanguage(): TracedLanguage {
    return if (name.endsWith(TracedLanguage.JAVA.fileExtension)) {
        TracedLanguage.JAVA
    } else if (name.endsWith(TracedLanguage.KOTLIN.fileExtension)) {
        TracedLanguage.KOTLIN
    } else {
        TracedLanguage.OTHER
    }
}

fun String.isAllWhitespace(): Boolean {
    return all { it.isWhitespace() }
}
