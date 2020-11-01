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

Library | Setup | Inject | Inject(Fragment)
--- | ---:| ---:| ---:
**Koin** | 0.429 ms | 0.103 ms | 0.132 ms
**Kodein** | 15.412 ms | 0.013 ms | 1.050 ms
**Katana** | 0.296 ms | 0.108 ms | 0.083 ms
**Custom** | 0.055 ms | 0.040 ms | 0.046 ms
**Dagger** | 0.002 ms | 0.040 ms | 0.153 ms
**DepPro** | 0.003 ms | 0.011 ms | 0.068 ms