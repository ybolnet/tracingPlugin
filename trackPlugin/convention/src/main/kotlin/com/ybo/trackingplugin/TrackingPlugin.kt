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

            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            tasks.register<ProcessTraceTask>(TraceProcessingParams.PROCESSING_TASK_NAME) {
                listOfConfigs = trackingConfig.configurationHandler.configs
                doLast {
                    println("PROCESSTRACE processing trace")
                }
            }

            tasks.register<UnprocessTraceTask>(TraceProcessingParams.REVERSE_PROCESSING_TASK_NAME) {
                listOfConfigs = trackingConfig.configurationHandler.configs
                doLast {
                    println("PROCESSTRACE Put trace annotation back to normal")
                }
            }

            afterEvaluate {
                trackingConfig.trackables.forEach { taskName ->
                    tasks.getByName(taskName).let {
                        tasks.register("${TraceProcessingParams.TRACABLE_TASKS_PREFIX}${it.name.capitalized()}") {
                            group = TraceProcessingParams.TASK_PROCESSING_GROUP
                            // Hook prepareBuild to execute before the assemble task
                            dependsOn(TraceProcessingParams.PROCESSING_TASK_NAME)

                            // Hook trackable to execute after prepareBuild
                            dependsOn(it.name)

                            // Hook postpareBuild to execute after the trackable task
                            finalizedBy(TraceProcessingParams.REVERSE_PROCESSING_TASK_NAME)
                            it.finalizedBy(TraceProcessingParams.REVERSE_PROCESSING_TASK_NAME)
                        }
                    }
                }
            }
        }
    }
}
