package com.sloydev.dependencyinjectionperformance.koin

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.FibonacciJava
import com.sloydev.dependencyinjectionperformance.FibonacciKotlin
import com.sloydev.dependencyinjectionperformance.Library.Koin
import com.sloydev.dependencyinjectionperformance.Variant
import com.sloydev.dependencyinjectionperformance.measureTime
import org.koin.android.ext.android.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class KoinFragment : Fragment() {
    private val javaFib8: FibonacciJava.Fib8 by inject()
    private val kotlinFib8: FibonacciKotlin.Fib8 by inject()
    override fun onResume() {
        super.onResume()
        startKoin {
            modules(koinKotlinModule)
            modules(koinJavaModule)
        }
        val injectionTimeJava = measureTime { javaFib8 }
        val injectionTimeKotlin = measureTime { kotlinFib8 }
        stopKoin()
        parentFragmentManager.setFragmentResult(
            this.javaClass.simpleName,
            bundleOf(
                Variant.JAVA.name to injectionTimeJava,
                Variant.KOTLIN.name to injectionTimeKotlin
            )
        )
    }
}