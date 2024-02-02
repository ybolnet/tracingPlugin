package com.ybo.trackingplugin.tasks

import com.ybo.trackingplugin.extension.TraceConfig
import com.ybo.trackingplugin.tasks.data.TraceAnnotationMark
import com.ybo.trackingplugin.tasks.data.getLanguage
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import java.io.File

open class BrowsingTask : DefaultTask() {

    init {
        group = TraceProcessingParams.TASK_PROCESSING_GROUP
    }

    @Input
    var listOfConfigs: List<TraceConfig> = emptyList()

    data class TrackedFile(
        val file: File,
        val toBeProcessedMarkToTrack: TraceAnnotationMark,
        val alreadyProcessedMarkToTrack: TraceAnnotationMark,
    )

    fun browseCode(block: (file: TrackedFile, conf: TraceConfig) -> Unit) {
        listOfConfigs.forEach { config ->
            println("browsing file for config $config")
            val fileTree: FileTree =
                project.fileTree(config.srcPath + "")
                    .matching {
                        include("**/*.kt")
                        include("**/*.java")
                        if (config.exclude?.isNotEmpty() == true) {
                            config.exclude?.let { it1 -> exclude(*it1) }
                        }
                    }
            println("tree ${fileTree.files} ")

            fileTree.forEach {
                println("dealing with file ${it.name}")
                if (it.canRead()) {
                    println("we can process ${it.name}")
                    val processedMarkToTrack =
                        TraceAnnotationMark(config.alreadyProcessedAnnotation, it.getLanguage())
                    val toBeProcessedMarkToTrack =
                        TraceAnnotationMark(config.toBeProcessedAnnotation, it.getLanguage())
                    block(
                        TrackedFile(
                            file = it,
                            toBeProcessedMarkToTrack = toBeProcessedMarkToTrack,
                            alreadyProcessedMarkToTrack = processedMarkToTrack,
                        ),
                        config,
                    )
                } else {
                    println("we cannot read ${it.name}")
                }
            }
        }
    }
}
