package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.extension.TraceConfig
import com.ybo.trackingplugin.tasks.BrowsingTask
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.data.TrackedSignal
import com.ybo.trackingplugin.tasks.data.getLanguage
import org.gradle.api.GradleException
import java.io.File

class SignalsExtractorImpl : BrowsingTask.SignalsExtractor {
    override fun extract(configs: List<TraceConfig>, file: File): List<TrackedSignal> {
        return configs.flatMap { it.extract(file) }
    }

    private fun TraceConfig.extract(file: File): List<TrackedSignal> {
        return annotation?.let { wholeAnnotationPath ->
            if (!wholeAnnotationPath.contains(".")) {
                throw GradleException("trace annotation must have the whole package")
            } else if (wholeAnnotationPath.endsWith(".") || wholeAnnotationPath.contains("..")) {
                throw GradleException("trace annotation is not well formatted ($wholeAnnotationPath)")
            }

            wholeAnnotationPath.split(".").partition {
                it[0].isLowerCase()
            }.let { (folderPartList, classPartList) ->
                val folderPart = folderPartList.asPath()
                val classPart = classPartList.asPath()
                if ("$folderPart.$classPart" != wholeAnnotationPath) {
                    throw GradleException(
                        "trace annotation is not well formatted ($wholeAnnotationPath vs" +
                            "$folderPart + $classPart)",
                    )
                }
                val finalList = mutableListOf(classPart)
                classPartList.fold("") { acc, item ->
                    if (acc.isNotEmpty()) {
                        "$acc.$item"
                    } else {
                        item
                    }.also {
                        classPart.replace(it, "").let { short ->
                            if (short.isNotEmpty()) {
                                finalList.add(short)
                            }
                        }
                    }
                }
                finalList.map { shortAnnotation ->
                    val reverseAnnotation = alreadyProcessedAnnotation()!!
                    TrackedSignal(
                        toBeProcessedMarkToTrack = TraceAnnotationMark(
                            wholeClass = wholeAnnotationPath,
                            language = file.getLanguage(),
                            shortVersion = "@$shortAnnotation",
                            longVersion = "@$wholeAnnotationPath",
                        ),
                        alreadyProcessedMarkToTrack = TraceAnnotationMark(
                            wholeClass = reverseAnnotation,
                            language = file.getLanguage(),
                            shortVersion = "@" + reverseAnnotation.substring(
                                reverseAnnotation.lastIndexOf(
                                    '.',
                                ) + 1,
                            ),
                            longVersion = "@$reverseAnnotation",
                        ),
                        tracerFactory = this.tracerFactory,
                    )
                }
            }
        } ?: throw GradleException("no trace annotation defined")
    }

    fun List<String>.asPath(): String {
        return fold("") { acc, str ->
            if (acc.isNotEmpty()) {
                "$acc.$str"
            } else {
                str
            }
        }
    }
}
