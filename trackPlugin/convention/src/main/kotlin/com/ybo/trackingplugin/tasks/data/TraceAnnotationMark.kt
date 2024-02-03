package com.ybo.trackingplugin.tasks.data

import org.gradle.api.GradleException

/** represents a annotation that user put on a method */
class TraceAnnotationMark(
    private val wholeClass: String?,
    val language: TracedLanguage,
) {
    val shortVersion: String
        get() {
            if (wholeClass == null) {
                throw GradleException("no trace annotation defined")
            } else if (!wholeClass.contains(".")) {
                throw GradleException("trace annotation must have the whole package")
            } else if (wholeClass.endsWith(".")) {
                throw GradleException("trace annotation is not well formatted ($wholeClass)")
            }
            return "@" + wholeClass.substring(wholeClass.lastIndexOf('.')+1)
        }

    val longVersion: String
        get() {
            if (wholeClass == null) {
                throw GradleException("no trace annotation defined")
            } else if (!wholeClass.contains(".")) {
                throw GradleException("trace annotation must have the whole package")
            } else if (wholeClass.endsWith(".")) {
                throw GradleException("trace annotation is not well formatted ($wholeClass)")
            }
            return "@$wholeClass"
        }

    val importStatement: String
        get() = ("import $wholeClass") + when (language) {
            TracedLanguage.JAVA -> ";"
            TracedLanguage.KOTLIN -> ""
            TracedLanguage.OTHER -> ""
        }
}
