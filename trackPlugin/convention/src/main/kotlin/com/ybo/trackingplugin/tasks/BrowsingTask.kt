package com.ybo.trackingplugin.tasks

import com.ybo.trackingplugin.TrackingPlugin
import com.ybo.trackingplugin.extension.TraceConfig
import com.ybo.trackingplugin.tasks.data.TrackedFile
import com.ybo.trackingplugin.tasks.data.TrackedSignal
import com.ybo.trackingplugin.tasks.utils.createSignalsExtractor
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import java.io.File

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

    /** class extracting all signals ([TrackedSignal]) to look for in the client code from list of configurations
     * provided by user
     */
    interface SignalsExtractor {
        fun extract(configs: List<TraceConfig>, file: File): List<TrackedSignal>
    }

    fun forEachFile(block: (file: TrackedFile) -> Unit) {
        val signalsExtractor = createSignalsExtractor()
        val fileTree: FileTree =
            project.fileTree(srcPath + "")
                .matching {
                    include("**/*.kt")
                    include("**/*.java")
                    if (exclude.isNotEmpty()) {
                        exclude(*exclude)
                    }
                }

        fileTree.forEach {
            if (it.canRead()) {
                val signals = signalsExtractor.extract(listOfConfigs, it)
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
