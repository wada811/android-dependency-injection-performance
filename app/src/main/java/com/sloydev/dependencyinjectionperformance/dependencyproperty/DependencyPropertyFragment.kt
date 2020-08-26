package com.sloydev.dependencyinjectionperformance.dependencyproperty

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.FibonacciJava
import com.sloydev.dependencyinjectionperformance.FibonacciKotlin
import com.sloydev.dependencyinjectionperformance.Variant
import com.sloydev.dependencyinjectionperformance.measureTime
import com.wada811.dependencyproperty.dependency

class DependencyPropertyFragment : Fragment() {
    private val javaFib8 by dependency<DependencyPropertyJavaModule, FibonacciJava.Fib8> { it.fib8 }
    private val kotlinFib8 by dependency<DependencyPropertyKotlinModule, FibonacciKotlin.Fib8> { it.fib8 }
    override fun onResume() {
        super.onResume()
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