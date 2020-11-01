package com.sloydev.dependencyinjectionperformance.koin

import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.Fibonacci
import com.sloydev.dependencyinjectionperformance.measureTime
import com.sloydev.dependencyinjectionperformance.setInjectionTimeResult
import org.koin.android.ext.android.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class KoinFragment : Fragment() {
    private val fib8: Fibonacci.Fib8 by inject()
    override fun onResume() {
        super.onResume()
        startKoin {
            modules(koinModule)
        }
        val injectionTime = measureTime { fib8 }
        stopKoin()
        setInjectionTimeResult(injectionTime)
    }
}