package com.sloydev.dependencyinjectionperformance.kodein

import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.Fibonacci
import com.sloydev.dependencyinjectionperformance.measureTime
import com.sloydev.dependencyinjectionperformance.setInjectionTimeResult
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

class KodeinFragment : Fragment(), DIAware {
    override val di: DI by closestDI { requireContext() }
    private val fib8: Fibonacci.Fib8 by instance()
    override fun onResume() {
        super.onResume()
        val injectionTime = measureTime { fib8 }
        setInjectionTimeResult(injectionTime)
    }
}