package com.ybo.trackingplugin.extension

import org.gradle.api.Action

open class TrackingPluginExtension {
    /** name of tasks that will be enclosed with processTrace and unProcessTask.
     * these task will deal with the code modified to be traced, and when done will
     * leave the code as they have found them.
     * */
    var trackables: Array<String> = emptyArray()

    /** Configure the inner DSL object, [TraceProcessConfigHandler]. */
    val configurationHandler: TraceProcessConfigHandler = TraceProcessConfigHandler()

    /** Configure the inner DSL object, [TraceProcessConfigHandler]. */
    fun config(action: Action<TraceProcessConfigHandler>) {
        action.execute(configurationHandler)
    }
}
