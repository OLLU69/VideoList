package ollu.dp.ua.videolist.features.movies

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ollu.dp.ua.videolist.R
import ollu.dp.ua.videolist.core.interactor.GetMovieDetails
import ollu.dp.ua.videolist.core.interactor.PlayMovie
import javax.inject.Inject

class MovieDetailsViewModel
@Inject constructor(private val getMovieDetails: GetMovieDetails, private val playMovie: PlayMovie) : ViewModel() {

    val movieSummary = ObservableField<String>()
    val movieCast = ObservableField<String>()
    val movieDirector = ObservableField<String>()
    val movieYear = ObservableField<String>()
    val onMovieDetailsReady = MutableLiveData<Boolean>()

    val toolbarTitle = MutableLiveData<String>()
    val loadPosterUrlAndPostponeEnterTransition = MutableLiveData<String>()
    val notify = MutableLiveData<Int>()
    val onError = MutableLiveData<Int>()
    private var playListener: () -> Unit = {}
    private val handleMovieDetails: (MovieDetails) -> Unit = { movie: MovieDetails ->

        movie.apply {
            loadPosterUrlAndPostponeEnterTransition.value = poster
            toolbarTitle.value = title
            movieSummary.set(summary)
            movieCast.set(cast)
            movieDirector.set(director)
            movieYear.set(if (year == 0) "----" else year.toString())
            if (trailer.isEmpty()) {
                playListener = {
                    notify.value = (R.string.no_trailers)
                }
            } else {
                playListener = {
                    playMovie(trailer)
                }
            }
            onMovieDetailsReady.value = true
        }
    }

    private val handleFailure: (String) -> Unit = { failure ->
        when (failure) {
            "NetworkConnection" -> onError.value = R.string.failure_network_connection
            "ServerError" -> onError.value = R.string.failure_server_error
            "NonExistentMovie" -> onError.value = R.string.failure_movie_non_existent
            else -> onError.value = R.string.unknown_error
        }
    }

    fun loadMovieDetails(movieId: Int) = getMovieDetails.getDetails(movieId, handleMovieDetails, handleFailure)
    @Suppress("UNUSED_PARAMETER")
    fun moviePlayListener(view: View) {
        playListener()
    }

    private fun playMovie(url: String) {
        playMovie.play(url)
    }

}