package com.ybo.trackingplugin

import com.ybo.trackingplugin.extension.TrackingPluginExtension
import com.ybo.trackingplugin.tasks.ProcessTraceTask
import com.ybo.trackingplugin.tasks.TraceProcessingParams
import com.ybo.trackingplugin.tasks.UnprocessTraceTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register

class TrackingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            val trackingConfig =
                project.extensions.create(
                    TraceProcessingParams.TRACING_FEATURE_CONFIG,
                    TrackingPluginExtension::class.java,
                )

            DEBUG = trackingConfig.debug

            with(pluginManager) {
                apply("org.jetbrains.kotlin.android")
            }

            tasks.register<ProcessTraceTask>(TraceProcessingParams.PROCESSING_TASK_NAME) {
                trackingConfig.check()
                listOfConfigs = trackingConfig.configurationHandler.configs
                srcPath = trackingConfig.srcPath
                exclude = trackingConfig.exclude
                doLast {
                    if (TrackingPlugin.DEBUG) println("PROCESSTRACE processing trace ")
                }
            }

            tasks.register<UnprocessTraceTask>(TraceProcessingParams.REVERSE_PROCESSING_TASK_NAME) {
                trackingConfig.check()
                listOfConfigs = trackingConfig.configurationHandler.configs
                srcPath = trackingConfig.srcPath
                exclude = trackingConfig.exclude
                doLast {
                    if (TrackingPlugin.DEBUG) println("PROCESSTRACE Put trace annotation back to normal")
                }
            }

            afterEvaluate {
                // SANDWICHING: creating a task (handler task)
                // so that when called, a target task (tracked task) will be preceded and followed
                // respectively by preprocessing of trace, and unprocessing of trace
                trackingConfig.trackables.forEach { taskName ->
                    tasks.getByName(taskName).let { trackedTask ->
                        val handlerTaskName =
                            "${TraceProcessingParams.TRACABLE_TASKS_PREFIX}${trackedTask.name.capitalized()}"
                        tasks.register(handlerTaskName) {
                            group = TraceProcessingParams.TASK_PROCESSING_GROUP
                            // so that the preprocessing precedes the tracked task
                            dependsOn(TraceProcessingParams.PROCESSING_TASK_NAME)

                            // So that the handler task trigger the tracked task
                            finalizedBy(trackedTask.name)

                            // So that the unprocessing follows the tracked task
                            finalizedBy(TraceProcessingParams.REVERSE_PROCESSING_TASK_NAME)
                            tasks.getByName(TraceProcessingParams.REVERSE_PROCESSING_TASK_NAME)
                                .mustRunAfter(trackedTask.name)
                            // order is: preprocessing -> handler task -> tracked task -> unprocessing
                        }
                    }
                }
            }
        }
    }



    companion object {
        var DEBUG: Boolean = true
    }
}
