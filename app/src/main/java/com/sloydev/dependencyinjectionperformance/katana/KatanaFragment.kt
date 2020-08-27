package com.sloydev.dependencyinjectionperformance.katana

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.Fibonacci
import com.sloydev.dependencyinjectionperformance.Milliseconds
import com.sloydev.dependencyinjectionperformance.measureTime
import com.sloydev.dependencyinjectionperformance.setInjectionTimeResult
import org.rewedigital.katana.Component
import org.rewedigital.katana.KatanaTrait
import org.rewedigital.katana.android.fragment.KatanaFragmentDelegate
import org.rewedigital.katana.android.fragment.fragmentDelegate
import org.rewedigital.katana.injectNow

class KatanaFragment : Fragment(), KatanaTrait {
    private val delegate: KatanaFragmentDelegate<KatanaFragment>
    override lateinit var component: Component
    private lateinit var fib8: Fibonacci.Fib8
    private var injectionTime: Milliseconds = 0.0

    init {
        delegate = fragmentDelegate { _, _ ->
            component = Component(listOf(katanaModule))
            injectionTime = measureTime {
                fib8 = injectNow()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        delegate.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setInjectionTimeResult(injectionTime)
    }
}