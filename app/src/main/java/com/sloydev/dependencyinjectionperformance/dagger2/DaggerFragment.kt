package com.sloydev.dependencyinjectionperformance.dagger2

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.FibonacciJava
import com.sloydev.dependencyinjectionperformance.Milliseconds
import com.sloydev.dependencyinjectionperformance.Variant
import com.sloydev.dependencyinjectionperformance.measureTime
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class DaggerFragment : Fragment(), HasAndroidInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> = androidInjector
    override fun onAttach(context: Context) {
        injectionTimeJava = measureTime {
        }
        injectionTimeKotlin = measureTime {
            AndroidSupportInjection.inject(this)
        }
        super.onAttach(context)
    }

    @Inject
    lateinit var javaFib8: FibonacciJava.Fib8
    private var injectionTimeJava: Milliseconds = 0.0
    private var injectionTimeKotlin: Milliseconds = 0.0

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