package com.ybo.trackingplugin.tasks

import com.ybo.trackingplugin.TrackingPlugin
import com.ybo.trackingplugin.extension.TraceConfig
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.data.TrackedFile
import com.ybo.trackingplugin.tasks.data.TrackedSignal
import com.ybo.trackingplugin.tasks.data.getLanguage
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input

/** Base task browsing through java and kotlin files, looking for signals .*/
open class BrowsingTask : DefaultTask() {

    init {
        group = TraceProcessingParams.TASK_PROCESSING_GROUP
    }

    @Input
    var listOfConfigs: List<TraceConfig> = emptyList()

    @Input
    var srcPath: String? = null

    @Input
    var exclude: Array<String> = emptyArray()

    fun forEachFile(block: (file: TrackedFile) -> Unit) {
        val fileTree: FileTree =
            project.fileTree(srcPath + "")
                .matching {
                    include("**/*.kt")
                    include("**/*.java")
                    if (exclude?.isNotEmpty() == true) {
                        exclude?.let { it1 -> exclude(*it1) }
                    }
                }

        fileTree.forEach {
            if (it.canRead()) {
                val signals = listOfConfigs.map { config ->
                    TrackedSignal(
                        alreadyProcessedMarkToTrack = TraceAnnotationMark(
                            config.alreadyProcessedAnnotation(),
                            it.getLanguage(),
                        ),
                        toBeProcessedMarkToTrack = TraceAnnotationMark(
                            config.annotation,
                            it.getLanguage(),
                        ),
                        tracerFactory = config.tracerFactory,
                    )
                }
                block(
                    TrackedFile(
                        file = it,
                        signals = signals,
                    ),
                )
            } else {
                if (TrackingPlugin.DEBUG) println("we cannot read ${it.name}. moving on.")
            }
        }
    }
}
