package com.sloydev.dependencyinjectionperformance.dagger2

import android.app.Application
import com.sloydev.dependencyinjectionperformance.ActivityBindingModule
import com.sloydev.dependencyinjectionperformance.App
import com.sloydev.dependencyinjectionperformance.FragmentBindingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityBindingModule::class,
        FragmentBindingModule::class,
//        DaggerKotlinModule::class,
//        DaggerJavaModule::class,
    ]
)
interface AppComponent : AndroidInjector<App> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}

fun Application.createAppComponent() = DaggerAppComponent.factory().create(this)