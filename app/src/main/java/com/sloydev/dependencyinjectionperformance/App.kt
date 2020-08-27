package com.sloydev.dependencyinjectionperformance

import com.sloydev.dependencyinjectionperformance.dagger2.createAppComponent
import com.sloydev.dependencyinjectionperformance.dependencyproperty.DependencyPropertyJavaModule
import com.sloydev.dependencyinjectionperformance.dependencyproperty.DependencyPropertyKotlinModule
import com.wada811.dependencyproperty.DependencyContext
import com.wada811.dependencyproperty.DependencyModules
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication(), DependencyContext {
    override val dependencyModules: DependencyModules by dependencyModules(DependencyPropertyJavaModule(), DependencyPropertyKotlinModule())
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component = createAppComponent()
        component.inject(this)
        return component
    }
}
