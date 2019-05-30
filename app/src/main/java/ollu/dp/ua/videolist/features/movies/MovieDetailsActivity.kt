package ollu.dp.ua.videolist.features.movies

import android.content.Context
import android.content.Intent
import ollu.dp.ua.videolist.core.platform.BaseActivity

class MovieDetailsActivity : BaseActivity() {

    companion object {
        const val MOVIE_ID = "MOVIE_ID"
        const val POSTER = "POSTER"

        fun callingIntent(context: Context, movie: Movie): Intent {
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra(MOVIE_ID, movie.id)
            intent.putExtra(POSTER, movie.poster)
            return intent
        }
    }

    override fun fragment() = MovieDetailsFragment.forMovie(intent.extras!!)
}
