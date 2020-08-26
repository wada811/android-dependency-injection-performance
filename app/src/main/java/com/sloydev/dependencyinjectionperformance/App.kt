package com.sloydev.dependencyinjectionperformance

import android.app.Application
import com.sloydev.dependencyinjectionperformance.dependencyproperty.DependencyPropertyJavaModule
import com.sloydev.dependencyinjectionperformance.dependencyproperty.DependencyPropertyKotlinModule
import com.wada811.dependencyproperty.DependencyContext
import com.wada811.dependencyproperty.DependencyModules

class App : Application(), DependencyContext {
    override val dependencyModules: DependencyModules by dependencyModules(DependencyPropertyJavaModule(), DependencyPropertyKotlinModule())
}
