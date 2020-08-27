package com.sloydev.dependencyinjectionperformance.dagger2

import com.sloydev.dependencyinjectionperformance.Library.Dagger.JavaDaggerTest
import com.sloydev.dependencyinjectionperformance.Library.Dagger.KotlinDaggerTest
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DaggerKotlinModule::class])
interface KotlinDaggerComponent {
    fun inject(injectionTest: KotlinDaggerTest)

    companion object {
        fun create() = DaggerKotlinDaggerComponent.create()
    }
}

@Singleton
@Component(modules = [DaggerJavaModule::class])
interface JavaDaggerComponent {
    fun inject(injectionTest: JavaDaggerTest)

    companion object {
        fun create() = DaggerJavaDaggerComponent.create()
    }
}
