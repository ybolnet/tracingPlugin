package com.ybo.trackingplugin.tasks.data

/** wrapper of a regex to look for in the code files. */
interface PatternToSearch {
    val name: String
    fun regex(): Regex
}
