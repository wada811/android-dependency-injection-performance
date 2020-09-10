package com.sloydev.dependencyinjectionperformance

import com.sloydev.dependencyinjectionperformance.dagger2.createAppComponent
import com.sloydev.dependencyinjectionperformance.dependencyproperty.DependencyPropertyModule
import com.sloydev.dependencyinjectionperformance.kodein.kodeinModule
import com.wada811.dependencyproperty.DependencyModules
import com.wada811.dependencyproperty.DependencyModulesHolder
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.kodein.di.DI
import org.kodein.di.DIAware

class App : DaggerApplication(), DependencyModulesHolder, DIAware {
    override val dependencyModules: DependencyModules by dependencyModules(DependencyPropertyModule())
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component = createAppComponent()
        component.inject(this)
        return component
    }

    override val di: DI by DI.lazy {
        import(kodeinModule)
    }
}
