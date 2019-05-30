package ollu.dp.ua.videolist.features.movies

import android.content.Context
import ollu.dp.ua.videolist.core.extension.networkInfo
import javax.inject.Inject
import javax.inject.Singleton

interface MoviesRepository {
    fun movies(page: Int = 1): Movies
    fun searchMovies(page: Int = 1, query: String): Movies
    fun movieDetails(movieId: Int): MovieDetails
}

/**
 * Injectable class which returns information about the network connection state.
 */
@Suppress("DEPRECATION")
@Singleton
class NetworkHandler
@Inject constructor(private val context: Context) {
    val isConnected get() = context.networkInfo?.isConnectedOrConnecting ?: false
}
