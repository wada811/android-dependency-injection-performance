package com.sloydev.dependencyinjectionperformance

import androidx.fragment.app.Fragment
import com.sloydev.dependencyinjectionperformance.Variant.JAVA
import com.sloydev.dependencyinjectionperformance.Variant.KOTLIN
import com.sloydev.dependencyinjectionperformance.custom.CustomFragment
import com.sloydev.dependencyinjectionperformance.custom.DIContainer
import com.sloydev.dependencyinjectionperformance.custom.customJavaModule
import com.sloydev.dependencyinjectionperformance.custom.customKotlinModule
import com.sloydev.dependencyinjectionperformance.dagger2.DaggerFragment
import com.sloydev.dependencyinjectionperformance.dagger2.DaggerJavaDaggerComponent
import com.sloydev.dependencyinjectionperformance.dagger2.DaggerKotlinDaggerComponent
import com.sloydev.dependencyinjectionperformance.dagger2.JavaDaggerComponent
import com.sloydev.dependencyinjectionperformance.dagger2.KotlinDaggerComponent
import com.sloydev.dependencyinjectionperformance.dependencyproperty.DependencyPropertyFragment
import com.sloydev.dependencyinjectionperformance.dependencyproperty.DependencyPropertyJavaModule
import com.sloydev.dependencyinjectionperformance.dependencyproperty.DependencyPropertyKotlinModule
import com.sloydev.dependencyinjectionperformance.katana.KatanaFragment
import com.sloydev.dependencyinjectionperformance.katana.katanaJavaModule
import com.sloydev.dependencyinjectionperformance.katana.katanaKotlinModule
import com.sloydev.dependencyinjectionperformance.kodein.KodeinFragment
import com.sloydev.dependencyinjectionperformance.kodein.kodeinJavaModule
import com.sloydev.dependencyinjectionperformance.kodein.kodeinKotlinModule
import com.sloydev.dependencyinjectionperformance.koin.KoinFragment
import com.sloydev.dependencyinjectionperformance.koin.koinJavaModule
import com.sloydev.dependencyinjectionperformance.koin.koinKotlinModule
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
        override fun setup(variant: Variant) {
            when (variant) {
                KOTLIN -> startKoin { modules(koinKotlinModule) }
                JAVA -> startKoin { modules(koinJavaModule) }
            }
        }

        override fun test(variant: Variant) {
            when (variant) {
                KOTLIN -> get<FibonacciKotlin.Fib8>()
                JAVA -> get<FibonacciJava.Fib8>()
            }
        }

        override fun fragment(): KoinFragment = KoinFragment()

        override fun teardown(variant: Variant) {
            stopKoin()
        }
    }

    object Kodein : Library<KodeinFragment>("Kodein") {
        private lateinit var kodein: DI
        override fun setup(variant: Variant) {
            kodein = when (variant) {
                KOTLIN -> DI { import(kodeinKotlinModule) }
                JAVA -> DI { import(kodeinJavaModule) }
            }
        }

        override fun test(variant: Variant) {
            when (variant) {
                KOTLIN -> kodein.instance<FibonacciKotlin.Fib8>()
                JAVA -> kodein.instance<FibonacciJava.Fib8>()
            }
        }

        override fun fragment(): KodeinFragment = KodeinFragment()

        override fun teardown(variant: Variant) {
        }

    }

    object Katana : Library<KatanaFragment>("Katana") {
        init {
            org.rewedigital.katana.Katana.environmentContext = AndroidEnvironmentContext(profile = SPEED)
        }

        private lateinit var component: Component
        override fun setup(variant: Variant) {
            component = when (variant) {
                KOTLIN -> Component(listOf(katanaKotlinModule))
                JAVA -> Component(listOf(katanaJavaModule))
            }
        }

        override fun test(variant: Variant) {
            when (variant) {
                KOTLIN -> component.injectNow<FibonacciKotlin.Fib8>()
                JAVA -> component.injectNow<FibonacciJava.Fib8>()
            }
        }

        override fun fragment(): KatanaFragment = KatanaFragment()

        override fun teardown(variant: Variant) {
        }

    }

    object Custom : Library<CustomFragment>("Custom") {
        override fun setup(variant: Variant) {
            when (variant) {
                KOTLIN -> DIContainer.loadModule(customKotlinModule)
                JAVA -> DIContainer.loadModule(customJavaModule)
            }
        }

        override fun test(variant: Variant) {
            when (variant) {
                KOTLIN -> DIContainer.get<FibonacciKotlin.Fib8>()
                JAVA -> DIContainer.get<FibonacciJava.Fib8>()
            }
        }

        override fun fragment(): CustomFragment = CustomFragment()

        override fun teardown(variant: Variant) {
            DIContainer.unloadModules()
        }
    }

    object Dagger : Library<DaggerFragment>("Dagger") {
        private val kotlinDaggerTest = KotlinDaggerTest()
        private val javaDaggerTest = JavaDaggerTest()

        class KotlinDaggerTest {
            @Inject
            lateinit var daggerFib8: FibonacciKotlin.Fib8
        }

        class JavaDaggerTest {
            @Inject
            lateinit var daggerFib8: FibonacciJava.Fib8
        }

        private lateinit var kotlinComponent: KotlinDaggerComponent
        private lateinit var javaComponent: JavaDaggerComponent
        override fun setup(variant: Variant) {
            when (variant) {
                KOTLIN -> kotlinComponent = DaggerKotlinDaggerComponent.create()
                JAVA -> javaComponent = DaggerJavaDaggerComponent.create()
            }
        }

        override fun test(variant: Variant) {
            when (variant) {
                KOTLIN -> kotlinComponent.inject(kotlinDaggerTest).also { kotlinDaggerTest.daggerFib8 }
                JAVA -> javaComponent.inject(javaDaggerTest).also { javaDaggerTest.daggerFib8 }
            }
        }

        override fun fragment(): DaggerFragment = DaggerFragment()

        override fun teardown(variant: Variant) {
        }
    }

    object DependencyProperty : Library<DependencyPropertyFragment>("DepPro") {
        private lateinit var kotlinModule: DependencyPropertyKotlinModule
        private lateinit var javaModule: DependencyPropertyJavaModule
        override fun setup(variant: Variant) {
            when (variant) {
                KOTLIN -> kotlinModule = DependencyPropertyKotlinModule()
                JAVA -> javaModule = DependencyPropertyJavaModule()
            }
        }

        override fun test(variant: Variant) {
            when (variant) {
                KOTLIN -> kotlinModule.fib8
                JAVA -> javaModule.fib8
            }
        }

        override fun fragment(): DependencyPropertyFragment = DependencyPropertyFragment()

        override fun teardown(variant: Variant) {
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

    abstract fun setup(variant: Variant)
    protected abstract fun test(variant: Variant)
    abstract fun fragment(): TFragment
    protected abstract fun teardown(variant: Variant)

    fun runTest(): LibraryResult {
        log("Running $displayName...")
        return LibraryResult(displayName, Variant.values().map { it to runTest(it) }.toMap())
    }

    private fun runTest(variant: Variant): TestResult {
        val rounds = 100
        val startup = (1..rounds).map { measureTime { setup(variant) }.also { teardown(variant) } }
        setup(variant)

        val testDurations = (1..rounds).map { measureTime { test(variant) } }
        teardown(variant)
        return TestResult(startup, testDurations)
    }
}