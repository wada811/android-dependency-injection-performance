package com.sloydev.dependencyinjectionperformance.dagger2

import com.sloydev.dependencyinjectionperformance.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}