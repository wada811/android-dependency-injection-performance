package com.sloydev.dependencyinjectionperformance.dagger2

import com.sloydev.dependencyinjectionperformance.Library.Dagger.JavaDaggerTest
import com.sloydev.dependencyinjectionperformance.Library.Dagger.KotlinDaggerTest
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DaggerKotlinModule::class])
interface KotlinDaggerComponent {
    fun inject(injectionTest: KotlinDaggerTest)
}

@Singleton
@Component(modules = [DaggerJavaModule::class])
interface JavaDaggerComponent {
    fun inject(injectionTest: JavaDaggerTest)
}
