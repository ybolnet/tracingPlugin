package com.ybo.trackingplugin.tasks.data

import java.io.File

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
