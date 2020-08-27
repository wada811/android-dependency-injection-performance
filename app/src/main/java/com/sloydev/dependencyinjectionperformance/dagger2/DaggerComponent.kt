package com.sloydev.dependencyinjectionperformance.dagger2

import com.sloydev.dependencyinjectionperformance.Library.Dagger.DaggerTest
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DaggerModule::class])
interface DaggerComponent {
    fun inject(injectionTest: DaggerTest)

    companion object {
        fun create(): DaggerComponent = DaggerDaggerComponent.create()
    }
}
