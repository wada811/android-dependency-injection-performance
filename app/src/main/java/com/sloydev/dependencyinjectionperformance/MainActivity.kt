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
        showDeviceInfo()
        showInfoOnLogcat()
        Library.values().forEachIndexed { index, library ->
            val libraryResult = library.runTest()
            runTestOnFragment(library) { libraryFragmentResult ->
                reportOnLogcat(libraryResult, libraryFragmentResult)
                reportOnScreen(libraryResult, libraryFragmentResult)
                if (index == Library.values().lastIndex) {
                    addSeparator()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDeviceInfo() {
        findViewById<TextView>(R.id.deviceInfo).text = """
                ${Build.BRAND} · ${Build.DEVICE}
                Android ${Build.VERSION.RELEASE} (sdk ${Build.VERSION.SDK_INT})
            """.trimIndent()
    }

    private fun showInfoOnLogcat() {
        log("${Build.BRAND} ${Build.DEVICE} with Android ${Build.VERSION.RELEASE}\n")
        log(" ")
        log("Library | Setup | Inject | Inject(Fragment)\n")
        log("--- | ---:| ---:| ---:\n")
    }

    private fun reportOnLogcat(libraryResult: LibraryResult<Library<*>>, libraryFragmentResult: LibraryFragmentResult<out Library<*>>) {
        log("**${libraryResult.library.displayName}** | ${libraryResult.startupTime.median().format()} | ${libraryResult.injectionTime.median().format()} | ${libraryFragmentResult.injectionTime.median().format()}\n")
    }

    @SuppressLint("SetTextI18n")
    private fun reportOnScreen(results: LibraryResult<Library<*>>, libraryFragmentResult: LibraryFragmentResult<out Library<*>>) {
        val table = findViewById<TableLayout>(R.id.table)
        table += row(
            nameCell(results.library.displayName),
            timeCell(results.startupTime.median()),
            timeCell(results.injectionTime.median()),
            timeCell(libraryFragmentResult.injectionTime.median())
        )
    }

    private fun addSeparator() {
        val table = findViewById<TableLayout>(R.id.table)
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
        injectionTime: MutableList<Microseconds> = mutableListOf(),
        onComplete: (LibraryFragmentResult<Library<T>>) -> Unit
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
        supportFragmentManager.setFragmentResultListener(fragment.javaClass.simpleName, this) { _, result ->
            injectionTime.add(result.getDouble("injectionTime"))
            if (count < 100) {
                runTestOnFragment(library, count + 1, injectionTime, onComplete)
            } else {
                onComplete(LibraryFragmentResult(library, injectionTime))
            }
        }
    }
}
