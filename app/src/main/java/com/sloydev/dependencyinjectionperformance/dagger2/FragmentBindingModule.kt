package com.sloydev.dependencyinjectionperformance.dagger2

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBindingModule {

    @ContributesAndroidInjector(
        modules = [DaggerJavaModule::class, DaggerKotlinModule::class]
    )
    abstract fun contributeDaggerFragment(): DaggerFragment
}