package com.ybo.trackingplugin.extension

import org.gradle.api.Action

open class TrackingPluginExtension {
    var trackables: Array<String> = emptyArray()

    /** Configure the inner DSL object, [TheDeepStateHandler]. */
    val configurationHandler: TraceProcessConfigHandler = TraceProcessConfigHandler()

    /** Configure the inner DSL object, [TheDeepStateHandler]. */
    fun config(action: Action<TraceProcessConfigHandler>) {
        action.execute(configurationHandler)
    }
}
