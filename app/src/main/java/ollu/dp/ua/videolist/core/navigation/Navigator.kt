package ollu.dp.ua.videolist.core.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import ollu.dp.ua.videolist.features.movies.Movie
import ollu.dp.ua.videolist.features.movies.MovieDetailsActivity
import javax.inject.Inject
import javax.inject.Singleton


private const val VIDEO_URL_HTTP = "http://www.youtube.com/watch?v="
private const val VIDEO_URL_HTTPS = "https://www.youtube.com/watch?v="
const val SHOW_DETAILS = 100

@Singleton
class Navigator
@Inject constructor() {

    fun showMovieDetails(activity: FragmentActivity, movie: Movie, navigationExtras: Extras) {
        val intent = MovieDetailsActivity.callingIntent(activity, movie)
        val sharedView = navigationExtras.transitionSharedElement as ImageView
        val activityOptions =
            ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedView, sharedView.transitionName)
        activity.startActivityForResult(intent, SHOW_DETAILS, activityOptions.toBundle())
    }

    fun openVideo(context: Context, videoUrl: String) {
        try {
            context.startActivity(createYoutubeIntent(videoUrl))
        } catch (ex: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)))
        }
    }

    private fun createYoutubeIntent(videoUrl: String): Intent {
        val videoId = when {
            videoUrl.startsWith(VIDEO_URL_HTTP) -> videoUrl.replace(VIDEO_URL_HTTP, "")
            videoUrl.startsWith(VIDEO_URL_HTTPS) -> videoUrl.replace(VIDEO_URL_HTTPS, "")
            else -> videoUrl
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        intent.putExtra("force_fullscreen", true)

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return intent
    }

    class Extras(val transitionSharedElement: View)
}


