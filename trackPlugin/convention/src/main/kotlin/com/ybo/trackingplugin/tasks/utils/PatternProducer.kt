package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.utils.impl.patterns.PatternName

/**
 * Object able to produce patterns ([PatternToSearch]) which are to be looked for in a text.
 */
interface PatternProducer<out T : PatternName> {
    fun produce(): List<PatternToSearch<T>>
}
