package com.ybo.trackingplugin.tasks.utils.impl.patterns.sorters

import com.ybo.trackingplugin.tasks.data.TracedMethod
import com.ybo.trackingplugin.tasks.utils.ResultSorter

internal class MethodsSorter(private val text: String) : ResultSorter<TracedMethod> {

    override fun sort(list: List<TracedMethod>): List<TracedMethod> {
        return list
            .withPosition()
            .removeRepetitions()
            .sortedBy {
                it.line
            }.also {
                println("SORTING before ${list.map { it.name }}")
                println("SORTING after ${it.map { it.method.name + " l=" + it.line }}")
            }.map {
                it.method
            }
    }

    data class MethodWithPosition(
        val method: TracedMethod,
        val line: Int,
    )

    private fun List<MethodWithPosition>.removeRepetitions(): List<MethodWithPosition> {
        val methodsMap = mutableMapOf<MethodId, MethodWithPosition>()
        return this.mapNotNull { positionedMethod ->
            if (methodsMap.containsKey(positionedMethod.getId())) {
                null
            } else {
                methodsMap[positionedMethod.getId()] = positionedMethod
                positionedMethod
            }
        }
    }

    private fun MethodWithPosition.getId(): MethodId {
        return MethodId(
            name = this.method.name,
            line = this.line,
        )
    }

    data class MethodId(
        val name: String,
        val line: Int,
    )

    private fun List<TracedMethod>.withPosition(): List<MethodWithPosition> {
        val thingCountMap = mutableMapOf<TracedMethod, Int>()
        val result = mutableListOf<MethodWithPosition>()

        for (method in this) {
            val count = thingCountMap.getOrDefault(method, 0) + 1
            thingCountMap[method] = count
            result.add(
                MethodWithPosition(
                    method = method,
                    line = calculateLine(text, method.wholeSignature, count),
                ),
            )
        }

        return result
    }

    private fun calculateLine(text: String, needle: String, n: Int): Int {
        println("CALCULATE: $needle  $n")
        val needleNb = countOccurrences(text, needle)
        return if (needleNb > 0 && needleNb >= n && n > 0) {
            val subtexts = text.split(needle)
            var nbLines = 0
            for (i in 0 until n) {
                val subText = subtexts[i]
                nbLines += subText.filter { it == '\n' }.length
            }
            nbLines += (n - 1) * needle.filter { it == '\n' }.length
            println("CALCULATE: returning $nbLines")
            nbLines
        } else {
            -1
        }
    }

    private fun countOccurrences(text: String, substring: String): Int {
        return text.windowed(substring.length, 1) { it }.count { it == substring }
    }
}
