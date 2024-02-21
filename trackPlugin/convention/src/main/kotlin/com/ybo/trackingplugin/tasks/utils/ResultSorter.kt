package com.ybo.trackingplugin.tasks.utils

interface ResultSorter<ResultType> {
    fun sort(list: List<ResultType>): List<ResultType>

}
