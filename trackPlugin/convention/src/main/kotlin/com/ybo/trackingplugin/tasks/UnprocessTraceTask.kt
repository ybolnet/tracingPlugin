package com.ybo.trackingplugin.tasks

import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import org.gradle.api.tasks.TaskAction
import java.io.File

/** task removing all trace calls at the start of every traced method.
 * counterpart of [ProcessTraceTask]
 * */
open class UnprocessTraceTask : BrowsingTask() {

    @TaskAction
    fun unprocessTrace() {
        browseCode { tracked, _ ->
            unprocessTraceAnnotations(
                tracked.file,
                tracked.toBeProcessedMarkToTrack,
                tracked.alreadyProcessedMarkToTrack,
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
        if (!text.contains(processed.longVersion) && !text.contains(processed.shortVersion)) {
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
