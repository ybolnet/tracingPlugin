package com.ybo.trackingplugin.tasks

import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File

open class UnprocessTraceTask : BrowsingTask() {

    @TaskAction
    fun unprocessTrace() {
        if (pathForSourceCode == null) {
            throw GradleException("srcPath must be defined")
        }
        browseCode {
            unprocessTraceAnnotations(
                it.file,
                it.toBeProcessedMarkToTrack,
                it.alreadyProcessedMarkToTrack,
            )
        }
    }

    private fun unprocessTraceAnnotations(
        file: File,
        toBeProcessedMark: TraceAnnotationMark,
        processed: TraceAnnotationMark,
    ): Boolean {
        val tag = TraceProcessingParams.TAG
        val toProcessAnnotationShort = toBeProcessedMark.shortVersion
        val toProcessAnnotationLong = toBeProcessedMark.longVersion
        var text = file.readText()
        val tagCatchingRegex = Regex("\\s*/\\*$tag\\*/.*")
        val matcher = tagCatchingRegex.findAll(text)
        println("try unprocessing trace for file " + file.name)
        if (matcher.count() == 0 ||
            (
                !text.contains(processed.longVersion) &&
                    !text.contains(processed.shortVersion)
                )
        ) {
            println("file is not concerned by unprocessing. ")
            return false
        }
        println("unprocessing trace for file " + file.name)
        matcher.forEach {
            text = text.replace(it.value, "")
        }
        if (text.contains(toBeProcessedMark.importStatement)) {
            text = text
                .replace(processed.shortVersion, toProcessAnnotationShort)
                .replace(processed.longVersion, toProcessAnnotationShort)
        } else {
            text = text
                .replace(processed.shortVersion, toProcessAnnotationLong)
                .replace(processed.longVersion, toProcessAnnotationLong)
        }
        file.writeText(text)
        println("unprocessing trace done for file " + file.name)
        return true
    }
}
