package ollu.dp.ua.videolist.core.platform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*
import ollu.dp.ua.videolist.R
import ollu.dp.ua.videolist.core.extension.inTransaction

/**
 * Base Activity class with helper methods for handling fragment transactions and back button
 * events.
 * @see AppCompatActivity
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout)
        setSupportActionBar(toolbar)
        addFragment(savedInstanceState)
    }

    override fun onBackPressed() {
        currentFragment()?.onBackPressed()
        super.onBackPressed()
    }

    fun currentFragment() = (supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? BaseFragment<*>)

    private fun addFragment(savedInstanceState: Bundle?) = savedInstanceState
        ?: supportFragmentManager.inTransaction {
            add(R.id.fragmentContainer, fragment())
        }

    abstract fun fragment(): BaseFragment<*>
}
