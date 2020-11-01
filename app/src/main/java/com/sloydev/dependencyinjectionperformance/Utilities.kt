package com.sloydev.dependencyinjectionperformance

import android.content.res.Resources
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.system.measureNanoTime

typealias Microseconds = Double

data class LibraryResult<TLibrary>(
    val library: TLibrary,
    val startupTime: List<Microseconds>,
    val injectionTime: List<Microseconds>
)

data class LibraryFragmentResult<TLibrary>(
    val library: TLibrary,
    val injectionTime: List<Microseconds>
)

fun Microseconds?.format() = String.format(Locale.ENGLISH, "%.3f µs", this)

fun measureTime(block: () -> Unit): Microseconds = measureNanoTime(block) / 1000.0

fun List<Microseconds>.median() = sorted().let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 }


fun log(msg: String) {
    Log.i("DI-TEST", msg)
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Fragment.setInjectionTimeResult(injectionTime: Microseconds) {
    parentFragmentManager.setFragmentResult(
        this.javaClass.simpleName,
        bundleOf("injectionTime" to injectionTime)
    )
}
