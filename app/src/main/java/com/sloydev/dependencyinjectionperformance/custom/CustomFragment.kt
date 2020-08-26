package com.sloydev.dependencyinjectionperformance.custom

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.FibonacciJava
import com.sloydev.dependencyinjectionperformance.FibonacciKotlin
import com.sloydev.dependencyinjectionperformance.Library.Custom
import com.sloydev.dependencyinjectionperformance.Variant
import com.sloydev.dependencyinjectionperformance.measureTime

class CustomFragment : Fragment() {
    private val javaFib8: FibonacciJava.Fib8 by DIContainer.inject()
    private val kotlinFib8: FibonacciKotlin.Fib8 by DIContainer.inject()

    override fun onResume() {
        super.onResume()
        Custom.setup(Variant.JAVA)
        Custom.setup(Variant.KOTLIN)
        val injectionTimeJava = measureTime { javaFib8 }
        val injectionTimeKotlin = measureTime { kotlinFib8 }
        parentFragmentManager.setFragmentResult(
            this.javaClass.simpleName,
            bundleOf(
                Variant.JAVA.name to injectionTimeJava,
                Variant.KOTLIN.name to injectionTimeKotlin
            )
        )
    }
}