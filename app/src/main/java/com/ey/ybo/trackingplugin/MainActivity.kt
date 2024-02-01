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
import com.ey.ybo.trackingplugin.annotations.SimpleTrace
import com.ey.ybo.trackingplugin.annotations.TraceWithReturns
import com.ey.ybo.trackingplugin.annotations.tracers.withTrace
import com.ey.ybo.trackingplugin.ui.theme.TrackingPluginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        test(1, 2.2)
        test2()
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

@TraceWithReturns
fun test(p1: Int, p2: Double, p3: Int = 4): Int {
    android.util.Log.d("TESTO", "TESTIL")
    val b: Int = p1
    return (p1 + 1).withTrace()
}

@TraceWithReturns
fun test2() {
    val b: Int = 1
}

@TraceWithReturns
private fun test3() {
    val b: Int = 1
}

@SimpleTrace
private fun test4() {
    val b: Int = 1
}

fun interface IntPredicate {
    fun accept(i: Int): Boolean
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    test2()
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrackingPluginTheme {
        Greeting("Android")
    }
}

@SimpleTrace
@TraceWithReturns
inline fun <T> MainActivity.testExtension(p1: Int, p2: Int = 2): Double {
    return 0.0
}
