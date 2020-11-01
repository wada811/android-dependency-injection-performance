package com.sloydev.dependencyinjectionperformance

import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.custom.CustomFragment
import com.sloydev.dependencyinjectionperformance.custom.DIContainer
import com.sloydev.dependencyinjectionperformance.custom.customModule
import com.sloydev.dependencyinjectionperformance.dagger2.DaggerComponent
import com.sloydev.dependencyinjectionperformance.dagger2.DaggerFragment
import com.sloydev.dependencyinjectionperformance.dependencyproperty.DependencyPropertyFragment
import com.sloydev.dependencyinjectionperformance.dependencyproperty.DependencyPropertyModule
import com.sloydev.dependencyinjectionperformance.katana.KatanaFragment
import com.sloydev.dependencyinjectionperformance.katana.katanaModule
import com.sloydev.dependencyinjectionperformance.kodein.KodeinFragment
import com.sloydev.dependencyinjectionperformance.kodein.kodeinModule
import com.sloydev.dependencyinjectionperformance.koin.KoinFragment
import com.sloydev.dependencyinjectionperformance.koin.koinModule
import org.kodein.di.DI
import org.kodein.di.instance
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import org.rewedigital.katana.Component
import org.rewedigital.katana.android.environment.AndroidEnvironmentContext
import org.rewedigital.katana.android.environment.AndroidEnvironmentContext.Profile.SPEED
import javax.inject.Inject

sealed class Library<TFragment : Fragment>(val displayName: String) {
    object Koin : Library<KoinFragment>("Koin"), KoinComponent {
        override fun setup() {
            startKoin { modules(koinModule) }
        }

        override fun test() {
            get<Fibonacci.Fib8>()
        }

        override fun fragment(): KoinFragment = KoinFragment()

        override fun teardown() {
            stopKoin()
        }
    }

    object Kodein : Library<KodeinFragment>("Kodein") {
        private lateinit var kodein: DI
        override fun setup() {
            kodein = DI { import(kodeinModule) }
        }

        override fun test() {
            kodein.instance<Fibonacci.Fib8>()
        }

        override fun fragment(): KodeinFragment = KodeinFragment()

        override fun teardown() {
        }

    }

    object Katana : Library<KatanaFragment>("Katana") {
        init {
            org.rewedigital.katana.Katana.environmentContext = AndroidEnvironmentContext(profile = SPEED)
        }

        private lateinit var component: Component
        override fun setup() {
            component = Component(listOf(katanaModule))
        }

        override fun test() {
            component.injectNow<Fibonacci.Fib8>()
        }

        override fun fragment(): KatanaFragment = KatanaFragment()

        override fun teardown() {
        }

    }

    object Custom : Library<CustomFragment>("Custom") {
        override fun setup() {
            DIContainer.loadModule(customModule)
        }

        override fun test() {
            DIContainer.get<Fibonacci.Fib8>()
        }

        override fun fragment(): CustomFragment = CustomFragment()

        override fun teardown() {
            DIContainer.unloadModules()
        }
    }

    object Dagger : Library<DaggerFragment>("Dagger") {
        private val daggerTest = DaggerTest()

        class DaggerTest {
            @Inject
            lateinit var daggerFib8: Fibonacci.Fib8
        }

        private lateinit var component: DaggerComponent
        override fun setup() {
            component = DaggerComponent.create()
        }

        override fun test() {
            component.inject(daggerTest).also { daggerTest.daggerFib8 }
        }

        override fun fragment(): DaggerFragment = DaggerFragment()

        override fun teardown() {
        }
    }

    object DependencyProperty : Library<DependencyPropertyFragment>("DepPro") {
        private lateinit var module: DependencyPropertyModule
        override fun setup() {
            module = DependencyPropertyModule()
        }

        override fun test() {
            module.fib8
        }

        override fun fragment(): DependencyPropertyFragment = DependencyPropertyFragment()

        override fun teardown() {
        }
    }


    companion object {
        @Suppress("UNCHECKED_CAST")
        fun values() = listOf<Library<*>>(
            Koin,
            Kodein,
            Katana,
            Custom,
            Dagger,
            DependencyProperty
        )
    }

    abstract fun setup()
    protected abstract fun test()
    abstract fun fragment(): TFragment
    protected abstract fun teardown()

    fun runTest(): LibraryResult<Library<*>> {
        val rounds = 100
        val startup = (1..rounds).map { measureTime { setup() }.also { teardown() } }
        setup()

        val testDurations = (1..rounds).map { measureTime { test() } }
        teardown()
        return LibraryResult(this, startup, testDurations)
    }
}