package com.ey.ybo.trackingplugin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ey.ybo.trackingplugin.annotations.Bullshit
import com.ey.ybo.trackingplugin.annotations.Cocomerlo
import com.ey.ybo.trackingplugin.annotations.Trace
import com.ey.ybo.trackingplugin.annotations.lambdaWithTrace
import com.ey.ybo.trackingplugin.annotations.withTrace
import com.ey.ybo.trackingplugin.ui.theme.TrackingPluginTheme
import com.ybo.trackingplugin.tracerlib.defaulttracer.DefTraceTest

/**
ok as test
 */
class MainActivity : ComponentActivity() {
    @DefTraceTest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        test(1, 2.2, 3)
        test2()
        testar(1, 2) @DefTraceTest @Trace { b ->
            val v = 1
        }

        testardinho(
            1,
            2,
            {
                val b = 2
            }.lambdaWithTrace(),
            { i: Exception ->
                val v = 1
            }.lambdaWithTrace(),
        )
        setContent {
            TrackingPluginTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@DefTraceTest
fun test(p1: Int, p2: Double, p3: Int): Int? {
    android.util.Log.d("TESTO", "TESTIL")
    val b: Int = p1
    return (p1 + 1).withTrace()
}

@DefTraceTest
fun test2() {
    val b: Int = 1
}

@DefTraceTest
fun testar(b: Int?, c: Int, block: (a: Int) -> Unit) {
    block.invoke(1)
}

fun testardinho(b: Int, c: Int, success: () -> Unit, failure: (Exception) -> Unit) {
    // block.invoke()
    failure(java.lang.Exception("bob"))
    val v =1
}

@DefTraceTest
fun test3() {
    val b: Int = 1
}

@DefTraceTest
private suspend fun test4() {
    val b: Int = 1
}

@DefTraceTest
@Cocomerlo
@Bullshit
private suspend fun testBullshit() {
    val b: Int = 1
}

fun interface IntPredicate {
    fun accept(i: Int): Boolean
}

@Composable
@DefTraceTest
fun Greeting(name: String, modifier: Modifier = Modifier) {
    test2()
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@DefTraceTest
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrackingPluginTheme {
        Greeting("Android")
    }
}

// p1: Int, p2: Int, list: List<Int>
@DefTraceTest
inline fun <T> testExtension(p1: Int, p2: Double): Double {
    return 0.0
}

@DefTraceTest
fun List<Int>.testExto(): Int {
    return 1
}
