package com.sloydev.dependencyinjectionperformance

import com.sloydev.dependencyinjectionperformance.custom.DIContainer
import com.sloydev.dependencyinjectionperformance.custom.customJavaModule
import com.sloydev.dependencyinjectionperformance.custom.customKotlinModule
import com.sloydev.dependencyinjectionperformance.dagger2.DaggerJavaDaggerComponent
import com.sloydev.dependencyinjectionperformance.dagger2.DaggerKotlinDaggerComponent
import com.sloydev.dependencyinjectionperformance.dagger2.JavaDaggerComponent
import com.sloydev.dependencyinjectionperformance.dagger2.KotlinDaggerComponent
import com.sloydev.dependencyinjectionperformance.dependencyproperty.JavaDependencyPropertyModule
import com.sloydev.dependencyinjectionperformance.dependencyproperty.KotlinDependencyPropertyModule
import com.sloydev.dependencyinjectionperformance.katana.katanaJavaModule
import com.sloydev.dependencyinjectionperformance.katana.katanaKotlinModule
import com.sloydev.dependencyinjectionperformance.kodein.kodeinJavaModule
import com.sloydev.dependencyinjectionperformance.kodein.kodeinKotlinModule
import com.sloydev.dependencyinjectionperformance.koin.koinJavaModule
import com.sloydev.dependencyinjectionperformance.koin.koinKotlinModule
import org.kodein.di.DI
import org.kodein.di.instance
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import org.rewedigital.katana.Component
import org.rewedigital.katana.Katana
import org.rewedigital.katana.android.environment.AndroidEnvironmentContext
import org.rewedigital.katana.android.environment.AndroidEnvironmentContext.Profile.SPEED
import javax.inject.Inject

class InjectionTest : KoinComponent {

    private val kotlinDaggerTest = KotlinDaggerTest()
    private val javaDaggerTest = JavaDaggerTest()

    private val rounds = 100

    fun runTests(): List<LibraryResult> {
        val results = listOf(
            koinTest(),
            kodeinTest(),
            katanaTest(),
            customTest(),
            daggerTest(),
            dependencyPropertyTest()
        )
        return results
    }

    private fun runTest(
        setup: () -> Unit,
        test: () -> Unit,
        teardown: () -> Unit = {}
    ): TestResult {
        val startup = (1..rounds).map { measureTime { setup() }.also { teardown() } }
        setup()

        val testDurations = (1..rounds).map { measureTime { test() } }
        teardown()
        return TestResult(startup, testDurations)
    }

    private fun koinTest(): LibraryResult {
        log("Running Koin...")
        return LibraryResult("Koin", mapOf(
            Variant.KOTLIN to runTest(
                setup = {
                    startKoin {
                        modules(koinKotlinModule)
                    }
                },
                test = { get<FibonacciKotlin.Fib8>() },
                teardown = { stopKoin() }
            ),
            Variant.JAVA to runTest(
                setup = {
                    startKoin {
                        modules(koinJavaModule)
                    }
                },
                test = { get<FibonacciJava.Fib8>() },
                teardown = { stopKoin() }
            )
        ))
    }

    private fun kodeinTest(): LibraryResult {
        log("Running Kodein...")
        lateinit var kodein: DI
        return LibraryResult("Kodein", mapOf(
            Variant.KOTLIN to runTest(
                setup = { kodein = DI { import(kodeinKotlinModule) } },
                test = { kodein.instance<FibonacciKotlin.Fib8>() }
            ),
            Variant.JAVA to runTest(
                setup = { kodein = DI { import(kodeinJavaModule) } },
                test = { kodein.instance<FibonacciKotlin.Fib8>() }
            )
        ))
    }

    private fun katanaTest(): LibraryResult {
        log("Running Katana...")
        Katana.environmentContext = AndroidEnvironmentContext(profile = SPEED)
        lateinit var component: Component
        return LibraryResult("Katana", mapOf(
            Variant.KOTLIN to runTest(
                setup = { component = Component(listOf(katanaKotlinModule)) },
                test = { component.injectNow<FibonacciKotlin.Fib8>() }
            ),
            Variant.JAVA to runTest(
                setup = { component = Component(listOf(katanaJavaModule)) },
                test = { component.injectNow<FibonacciJava.Fib8>() }
            )
        ))
    }

    private fun customTest(): LibraryResult {
        log("Running Custom...")
        return LibraryResult("Custom", mapOf(
            Variant.KOTLIN to runTest(
                setup = { DIContainer.loadModule(customKotlinModule) },
                test = { DIContainer.get<FibonacciKotlin.Fib8>() },
                teardown = { DIContainer.unloadModules() }
            ),
            Variant.JAVA to runTest(
                setup = { DIContainer.loadModule(customJavaModule) },
                test = { DIContainer.get<FibonacciJava.Fib8>() },
                teardown = { DIContainer.unloadModules() }
            )
        ))
    }

    private fun daggerTest(): LibraryResult {
        log("Running Dagger...")
        lateinit var kotlinComponent: KotlinDaggerComponent
        lateinit var javaComponent: JavaDaggerComponent
        return LibraryResult("Dagger", mapOf(
            Variant.KOTLIN to runTest(
                setup = { kotlinComponent = DaggerKotlinDaggerComponent.create() },
                test = { kotlinComponent.inject(kotlinDaggerTest) }
            ),
            Variant.JAVA to runTest(
                setup = { javaComponent = DaggerJavaDaggerComponent.create() },
                test = { javaComponent.inject(javaDaggerTest) }
            )
        ))
    }

    class KotlinDaggerTest {
        @Inject
        lateinit var daggerFib8: FibonacciKotlin.Fib8
    }

    class JavaDaggerTest {
        @Inject
        lateinit var daggerFib8: FibonacciJava.Fib8
    }

    private fun dependencyPropertyTest(): LibraryResult {
        log("Running DependencyProperty...")
        lateinit var kotlinModule: KotlinDependencyPropertyModule
        lateinit var javaModule: JavaDependencyPropertyModule
        return LibraryResult("DepPro", mapOf(
            Variant.KOTLIN to runTest(
                setup = { kotlinModule = KotlinDependencyPropertyModule() },
                test = { kotlinModule.fib8 }
            ),
            Variant.JAVA to runTest(
                setup = { javaModule = JavaDependencyPropertyModule() },
                test = { javaModule.fib8 }
            )
        ))
    }

}
