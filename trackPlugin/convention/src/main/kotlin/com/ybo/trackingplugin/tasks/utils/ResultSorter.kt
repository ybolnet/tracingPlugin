package com.ybo.trackingplugin.tasks.utils

/** object sorting the results of a pattern search*/
interface ResultSorter<ResultType> {
    fun sort(list: List<ResultType>): List<ResultType>
}
