package com.sloydev.dependencyinjectionperformance

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.plusAssign
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            runTests()
        }

        runTests()
    }

    private fun runTests() {
        val libraries = Library.values()
        val results = libraries.map { it.runTest() }
        reportOnLogcat(results)
        reportOnScreen(results)

        libraries.forEach {
            runTestOnFragment(it)
        }
    }

    private fun reportOnLogcat(results: List<LibraryResult>) {
        log("Done!\n")
        log("\n")
        log("${Build.BRAND} ${Build.DEVICE} with Android ${Build.VERSION.RELEASE}\n")
        log("\n")
        log("Library | Setup Kotlin | Setup Java | Inject Kotlin | Inject Java\n")
        log("--- | ---:| ---:| ---:| ---:\n")
        results.forEach {
            log("**${it.injectorName}** | ${it[Variant.KOTLIN].startupTime.median().format()} | ${it[Variant.JAVA].startupTime.median().format()}  | ${it[Variant.KOTLIN].injectionTime.median().format()} | ${it[Variant.JAVA].injectionTime.median().format()}\n")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun reportOnScreen(results: List<LibraryResult>) {
        findViewById<TextView>(R.id.deviceInfo).text = """
                ${Build.BRAND} · ${Build.DEVICE}
                Android ${Build.VERSION.RELEASE} (sdk ${Build.VERSION.SDK_INT})
            """.trimIndent()

        val table = findViewById<TableLayout>(R.id.table)
        results.forEach { result ->
            table += row(
                nameCell(result.injectorName),
                timeCell(result[Variant.KOTLIN].startupTime.median()),
                timeCell(result[Variant.JAVA].startupTime.median()),
                timeCell(result[Variant.KOTLIN].injectionTime.median()),
                timeCell(result[Variant.JAVA].injectionTime.median())
            )
        }

        table.addView(
            separator(),
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1.dp)
        )
    }

    private fun timeCell(time: Double): TextView {
        return TextView(this).apply {
            text = time.format()
            gravity = GravityCompat.END
        }
    }

    private fun nameCell(name: String): TextView {
        return TextView(this).apply {
            text = name
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun textCell(text: String): TextView {
        return TextView(this).also {
            it.text = text
            it.gravity = GravityCompat.END
        }
    }

    private fun row(vararg cells: View): TableRow {
        return TableRow(this).also { row ->
            cells.forEach { cell ->
                row += cell
            }
            row.updatePadding(
                top = 4.dp,
                bottom = 4.dp
            )
        }
    }

    private fun separator(): View {
        return View(this).apply {
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
            updatePadding(
                top = 4.dp,
                bottom = 4.dp
            )
        }
    }

    private fun <T : Fragment> runTestOnFragment(
        library: Library<T>,
        count: Int = 1,
        injectionTimeJava: MutableList<Milliseconds> = mutableListOf(),
        injectionTimeKotlin: MutableList<Milliseconds> = mutableListOf()
    ) {
        val fragment = library.fragment()
        supportFragmentManager.findFragmentByTag(fragment.javaClass.simpleName)?.let {
            supportFragmentManager.beginTransaction()
                .remove(it)
                .commit()
        }
        supportFragmentManager.beginTransaction()
            .add(library.fragment(), fragment.javaClass.simpleName)
            .commit()
        supportFragmentManager.clearFragmentResultListener(fragment.javaClass.simpleName)
        supportFragmentManager.setFragmentResultListener(
            fragment.javaClass.simpleName,
            this,
            { _, result ->
                injectionTimeJava.add(result.getDouble(Variant.JAVA.name))
                injectionTimeKotlin.add(result.getDouble(Variant.KOTLIN.name))
                if (count < 100) {
                    runTestOnFragment(library, count + 1, injectionTimeJava, injectionTimeKotlin)
                } else {
                    val uiResult = LibraryUiResult(
                        library.displayName,
                        mapOf(Variant.KOTLIN to injectionTimeKotlin, Variant.JAVA to injectionTimeJava)
                    )
                    reportUiResultOnLogcat(listOf(uiResult))
                    reportUiResultOnScreen(listOf(uiResult))
                }
            }
        )
    }

    private fun reportUiResultOnLogcat(results: List<LibraryUiResult>) {
        log("Done!\n")
        log("\n")
        log("${Build.BRAND} ${Build.DEVICE} with Android ${Build.VERSION.RELEASE}\n")
        log("\n")
        log("Library | Inject Kotlin | Inject Java\n")
        log("--- | ---:| ---:\n")
        results.forEach {
            log("**${it.injectorName}** | ${it[Variant.KOTLIN].median().format()} | ${it[Variant.JAVA].median().format()}\n")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun reportUiResultOnScreen(results: List<LibraryUiResult>) {
        val table = findViewById<TableLayout>(R.id.table)
        results.forEach { result ->
            table += row(
                nameCell(result.injectorName),
                textCell("inject in"),
                textCell("Fragment"),
                timeCell(result[Variant.KOTLIN].median()),
                timeCell(result[Variant.JAVA].median())
            )
        }

        table.addView(
            separator(),
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1.dp)
        )
    }
}
