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
import com.ey.ybo.trackingplugin.annotations.Trace
import com.ey.ybo.trackingplugin.ui.theme.TrackingPluginTheme
import com.ybo.trackingplugin.tracerlib.defaulttracer.DefTraceTest

/**
ok @DefTraceTest as test
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        test(1, 2.2)
        test2()
        testar(1, 2) @Bullshit @DefTraceTest @Trace { b ->
            val v = 1
        }

        testarde(1, 2) @Bullshit @DefTraceTest @Trace {
            val v = 1
        }
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
fun test(p1: Int, p2: Double, p3: Int = 4): Int {
    android.util.Log.d("TESTO", "TESTIL")
    val b: Int = p1
    return (p1 + 1)
}

@DefTraceTest
fun test2() {
    val b: Int = 1
}

@DefTraceTest
fun testar(b: Int, c: Int, block: (a: Int) -> Unit) {
    block.invoke(1)
}

@DefTraceTest
fun testarde(b: Int, c: Int, block: () -> Unit) {
    block.invoke()
}

@DefTraceTest
fun test3() {
    val b: Int = 1
}

@DefTraceTest
private suspend fun test4() {
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

@DefTraceTest
inline fun <T> MainActivity.testExtension(p1: Int, p2: Int = 2): Double {
    return 0.0
}
