package com.ybo.trackingplugin.tasks.utils

import com.ybo.trackingplugin.tasks.data.PatternToSearch
import com.ybo.trackingplugin.tasks.utils.impl.patterns.PatternName

/**
 * Object able to produce patterns ([PatternToSearch]) which are to be looked for in a text.
 */
interface PatternProducer<out T : PatternName> {

    /**
     * creates a list of patterns to search in a code file.
     * for performance purposes, the more costly patterns should be moved to the end of the list
     */
    fun produce(): List<PatternToSearch<T>>
}
