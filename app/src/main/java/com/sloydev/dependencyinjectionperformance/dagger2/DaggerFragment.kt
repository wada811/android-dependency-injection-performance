package com.sloydev.dependencyinjectionperformance.dagger2

import android.content.Context
import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.Fibonacci
import com.sloydev.dependencyinjectionperformance.Microseconds
import com.sloydev.dependencyinjectionperformance.measureTime
import com.sloydev.dependencyinjectionperformance.setInjectionTimeResult
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
        injectionTime = measureTime {
            AndroidSupportInjection.inject(this)
        }
        super.onAttach(context)
    }

    @Inject
    lateinit var fib8: Fibonacci.Fib8
    private var injectionTime: Microseconds = 0.0

    override fun onResume() {
        super.onResume()
        setInjectionTimeResult(injectionTime)
    }
}