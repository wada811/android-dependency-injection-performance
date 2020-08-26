package com.sloydev.dependencyinjectionperformance.katana

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.FibonacciJava
import com.sloydev.dependencyinjectionperformance.FibonacciKotlin
import com.sloydev.dependencyinjectionperformance.Milliseconds
import com.sloydev.dependencyinjectionperformance.Variant
import com.sloydev.dependencyinjectionperformance.measureTime
import org.rewedigital.katana.Component
import org.rewedigital.katana.KatanaTrait
import org.rewedigital.katana.android.fragment.KatanaFragmentDelegate
import org.rewedigital.katana.android.fragment.fragmentDelegate
import org.rewedigital.katana.injectNow

class KatanaFragment : Fragment(), KatanaTrait {
    private val delegate: KatanaFragmentDelegate<KatanaFragment>
    override lateinit var component: Component
    private lateinit var javaFib8: FibonacciJava.Fib8
    private lateinit var kotlinFib8: FibonacciKotlin.Fib8
    private var injectionTimeJava: Milliseconds = 0.0
    private var injectionTimeKotlin: Milliseconds = 0.0

    init {
        delegate = fragmentDelegate { _, _ ->
            component = Component(listOf(katanaKotlinModule, katanaJavaModule))

            injectionTimeJava = measureTime {
                javaFib8 = injectNow()
            }
            injectionTimeKotlin = measureTime {
                kotlinFib8 = injectNow()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        delegate.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        parentFragmentManager.setFragmentResult(
            this.javaClass.simpleName,
            bundleOf(
                Variant.JAVA.name to injectionTimeJava,
                Variant.KOTLIN.name to injectionTimeKotlin
            )
        )
    }
}