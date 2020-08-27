package com.sloydev.dependencyinjectionperformance.dependencyproperty

import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.Fibonacci
import com.sloydev.dependencyinjectionperformance.measureTime
import com.sloydev.dependencyinjectionperformance.setInjectionTimeResult
import com.wada811.dependencyproperty.dependency

class DependencyPropertyFragment : Fragment() {
    private val fib8 by dependency<DependencyPropertyModule, Fibonacci.Fib8> { it.fib8 }
    override fun onResume() {
        super.onResume()
        val injectionTime = measureTime { fib8 }
        setInjectionTimeResult(injectionTime)
    }
}