package ollu.dp.ua.videolist.movies_db

import ollu.dp.ua.videolist.features.movies.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

private const val NO_NAME = "No name"

class MoviesDb
@Inject constructor(
    private val networkHandler: NetworkHandler,
    private val service: MoviesDBService
) : MoviesRepository {
    override fun searchMovies(page: Int, query: String): Movies {
        return query({ service.api.searchMovies(page, query) }, { it.toMovies() })
    }

    override fun movies(page: Int): Movies {
        return query({ service.api.getMovies(page) }) { it.toMovies() }
    }

    override fun movieDetails(movieId: Int): MovieDetails {
        return query({ service.api.getDetails(movieId) }, { it.toMovieDetails() })
    }

    private fun getTrailer(movieId: Int): String {
        val trailers: Trailers = query({ service.api.getTrailer(movieId) })
        return trailers.results.firstOrNull { it.type == "Trailer" && it.site == "YouTube" }?.key ?: ""
    }

    private var peoples: Peoples? = null

    private fun getDirector(movieId: Int): String {
        if (peoples == null || peoples?.id != movieId) {
            peoples = query({ service.api.getPeoples(movieId) })
        }
        return peoples?.director ?: NO_NAME
    }

    private fun getCast(movieId: Int): String {
        if (peoples == null || peoples?.id != movieId) {
            peoples = query({ service.api.getPeoples(movieId) })
        }
        return peoples?.actors ?: NO_NAME
    }

    private fun <T, R> query(getCall: () -> Call<T>, transform: ((T) -> R)? = null): R {
        if (!networkHandler.isConnected) throw Exception("No connection")
        val call = getCall()
        val response = call.execute()
        if (response.isSuccessful) {
            val result = response.body() ?: throw Exception("Server error")
            @Suppress("UNCHECKED_CAST")
            return transform?.invoke(result) ?: result as R
        } else {
            throw Exception("Server error:" + response.errorBody()?.string())
        }
    }

    private fun MovieDetailsEntity.toMovieDetails(): MovieDetails {
        return MovieDetails(
            id,
            title,
            poster_path?.fullPosterPath,
            overview,
            getCast(id),
            getDirector(id),
            year,
            getTrailer(id)
        )
    }
}

class MoviesDBService @Inject constructor(retrofit: Retrofit) {
    val api: MoviesDBApi by lazy { retrofit.create(MoviesDBApi::class.java) }
}

private const val KEY = "e33d13bd03a3a95e8d4eee02f38ab8d7"
private const val GET_MOVIES = "3/discover/movie?api_key=$KEY&language=ru&sort_by=popularity.desc"
private const val SEARCH_MOVIES = "3/search/movie?api_key=$KEY&language=ru"
private const val GET_DETAILS = "3/movie/{id}?api_key=$KEY&language=ru"
private const val GET_TRAILERS = "3/movie/{id}/videos?api_key=$KEY&language=ru"
private const val GET_PEOPLES = "3/movie/{id}/credits?api_key=e33d13bd03a3a95e8d4eee02f38ab8d7&language=ru"

interface MoviesDBApi {
    @GET(GET_MOVIES)
    fun getMovies(@Query("page") page: Int): Call<MoviesResult>

    @GET(SEARCH_MOVIES)
    fun searchMovies(@Query("page") page: Int, @Query("query") query: String): Call<MoviesResult>

    @GET(GET_DETAILS)
    fun getDetails(@Path("id") movieId: Int): Call<MovieDetailsEntity>

    @GET(GET_TRAILERS)
    fun getTrailer(@Path("id") movieId: Int): Call<Trailers>

    @GET(GET_PEOPLES)
    fun getPeoples(@Path("id") int: Int): Call<Peoples>
}

