package com.ybo.trackingplugin.tasks.data

import com.ybo.trackingplugin.tasks.utils.impl.patterns.PatternName

/** wrapper of a regex to look for in the code files. */
interface PatternToSearch<out T : PatternName> {
    val name: T
    fun regex(): Regex
}
