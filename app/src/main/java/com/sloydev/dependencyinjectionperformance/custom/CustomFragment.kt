package com.sloydev.dependencyinjectionperformance.custom

import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.Fibonacci
import com.sloydev.dependencyinjectionperformance.measureTime
import com.sloydev.dependencyinjectionperformance.setInjectionTimeResult

class CustomFragment : Fragment() {
    private val fib8: Fibonacci.Fib8 by DIContainer.inject()

    override fun onResume() {
        super.onResume()
        DIContainer.loadModule(customModule)
        val injectionTime = measureTime { fib8 }
        setInjectionTimeResult(injectionTime)
    }
}