package com.ybo.trackingplugin.tasks.data

/** represents a case of a annotation mark that was not recognized as a tracked method */
data class Residual(val filename: String, val nbMarks: Int)
