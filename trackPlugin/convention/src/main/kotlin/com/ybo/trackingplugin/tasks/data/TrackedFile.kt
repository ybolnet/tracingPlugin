package com.ybo.trackingplugin.tasks.data

import java.io.File

/**
 * a file and all signals to be tracked in it.
 */
data class TrackedFile(
    val file: File,
    val signals: List<TrackedSignal>,
)
