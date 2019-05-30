package ollu.dp.ua.videolist.features.movies

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.view.Menu
import androidx.appcompat.widget.SearchView
import ollu.dp.ua.videolist.R
import ollu.dp.ua.videolist.core.navigation.SHOW_DETAILS
import ollu.dp.ua.videolist.core.platform.BaseActivity


class MoviesActivity : BaseActivity(), SearchView.OnQueryTextListener {

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            filer(query)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.search)?.actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(true)
            setOnQueryTextListener(this@MoviesActivity)
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filer(newText)
        return true
    }

    private fun filer(query: String?) {
        getFragment()?.filter(query)
    }

    private fun getFragment(): MoviesFragment? {
        return currentFragment() as MoviesFragment?
    }

    override fun fragment(): MoviesFragment {
        return MoviesFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != SHOW_DETAILS || resultCode == Activity.RESULT_CANCELED) return
        getFragment()?.notify(resultCode)
    }
}
