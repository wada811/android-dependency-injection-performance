package com.sloydev.dependencyinjectionperformance.kodein

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.FibonacciJava
import com.sloydev.dependencyinjectionperformance.FibonacciKotlin
import com.sloydev.dependencyinjectionperformance.Variant
import com.sloydev.dependencyinjectionperformance.measureTime
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class KodeinFragment : Fragment(), DIAware {
    override val di: DI by DI.lazy {
        import(kodeinKotlinModule)
        import(kodeinJavaModule)
    }
    private val javaFib8: FibonacciJava.Fib8 by instance()
    private val kotlinFib8: FibonacciKotlin.Fib8 by instance()
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