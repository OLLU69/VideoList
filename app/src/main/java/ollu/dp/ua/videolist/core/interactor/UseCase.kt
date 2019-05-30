package ollu.dp.ua.videolist.core.interactor

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ollu.dp.ua.videolist.core.navigation.Navigator
import ollu.dp.ua.videolist.features.movies.MovieDetails
import ollu.dp.ua.videolist.features.movies.MoviesRepository
import javax.inject.Inject
import javax.inject.Singleton

const val UNKNOWN_ERROR: String = "Неизвестная ошибка"

abstract class UseCase<out Type> where Type : Any {

    protected fun <R> query(onSuccess: (R) -> Unit, onError: (String) -> Unit, getResult: () -> R) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                onSuccess(withContext(Dispatchers.IO) {
                    getResult()
                })
            } catch (t: Throwable) {
                t.printStackTrace()
                onError(t.message ?: UNKNOWN_ERROR)
            }
        }
    }
}

@Singleton
class GetMovieDetails
@Inject constructor(private val moviesRepository: MoviesRepository) : UseCase<MovieDetails>() {
    fun getDetails(movieId: Int, handleMovieDetails: (MovieDetails) -> Unit, onError: (String) -> Unit) {
        query(handleMovieDetails, onError, { moviesRepository.movieDetails(movieId) })
    }

}

class PlayMovie
@Inject constructor(private val context: Context, private val navigator: Navigator) : UseCase<Unit>() {
    fun play(url: String) {
        navigator.openVideo(context, url)
    }
}
