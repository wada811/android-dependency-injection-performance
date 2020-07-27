# Android Injection Performance

This project aims to measure the performance of several Dependency Injection frameworks (or Service Locators) in different devices.

## Libraries tested
- [Koin](https://insert-koin.io/) - 2.1.16
- [Kodein](http://kodein.org/Kodein-DI/) - 7.3.0
- [Dagger 2](https://google.github.io/dagger/) - 2.28.3
- [Katana](https://github.com/rewe-digital-incubator/katana/) - 1.13.2
- [DependencyProperty](https://github.com/wada811/DependencyProperty/) - master(3caa9ffde5)

## The test
The test data are classes with dependencies in a structure similar to Fibonacci sequence, to simulate multiple levels of transitive dependencies.
For each library there is a test with Kotlin classes and one with Java classes, because some libraries seem to be affected by this difference.

Each test injects one of this dependencies 100 times and prints the maximum time, the minimum and the average.

The project contains an Android application that run the tests on its onCreate and prints the result to the Logcat.

The actual test is implemented in the class [InjectionTest.kt](https://github.com/Sloy/android-dependency-injection-performance/blob/master/app/src/main/java/com/sloydev/dependencyinjectionperformance/InjectionTest.kt)

## Results
Results can be quite different between different devices, so here are some results in different devices with different Android versions. Each table contains the **median** time of each library's setup and injection. More results are welcomed.

- [Pixel 3](#pixel-3)

### Pixel 3
google blueline with Android 10

Library | Setup Kotlin | Setup Java | Inject Kotlin | Inject Java
--- | ---:| ---:| ---:| ---:
**Koin** | 0.443 ms | 0.451 ms  | 0.126 ms | 0.058 ms
**Kodein** | 17.014 ms | 15.776 ms  | 0.015 ms | 0.014 ms
**Katana** | 0.291 ms | 0.296 ms  | 0.115 ms | 0.055 ms
**Custom** | 0.064 ms | 0.054 ms  | 0.042 ms | 0.032 ms
**Dagger** | 0.004 ms | 0.005 ms  | 0.027 ms | 0.032 ms
**DependencyProperty** | 0.003 ms | 0.003 ms  | 0.012 ms | 0.011 ms