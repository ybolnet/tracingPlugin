package com.ybo.trackingplugin.tasks.utils.impl

import com.ybo.trackingplugin.tasks.utils.CodeGenerator

class _CodeGeneratorKotlin : CodeGenerator() {
    override fun generate(
        params: String,
        tracerFactoryString: String,
        insideMethodIndentation: String,
        methodName: String,
        tag: String,
    ): String {
        return "/*$tag*/ $tracePerformerPackage.trace(" +
            tracerFactoryString + "(), " +
            "\"" + methodName + "\", " +
            "false, " +
            params.paramsOrNot() +")\n" +
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
