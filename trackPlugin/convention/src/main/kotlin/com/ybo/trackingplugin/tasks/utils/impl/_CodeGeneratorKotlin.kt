package com.ybo.trackingplugin.tasks.utils.impl

import com.ybo.trackingplugin.tasks.utils.CodeGenerator
import com.ybo.trackingplugin.toB64

class _CodeGeneratorKotlin : CodeGenerator() {
    override fun generate(
        params: String,
        tracerFactoryString: String,
        insideMethodIndentation: String,
        methodName: String,
        tag: String,
        alterationOffset: Int,
    ): String {
        return "/*$tag*/ $tracePerformerPackage.trace(" +
            tracerFactoryString + "(), " +
            "\"" + methodName.toB64() + "\", " +
            "false, " +
            params.paramsOrNot() + "," +
            alterationOffset + ")\n" +
            insideMethodIndentation
    }

    private fun String.paramsOrNot(): String {
        return if (isEmpty()) {
            "emptyArray()"
        } else {
            "arrayOf($this) "
        }
    }
}
