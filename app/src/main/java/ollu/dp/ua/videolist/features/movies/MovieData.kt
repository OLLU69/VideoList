package ollu.dp.ua.videolist.features.movies

import ollu.dp.ua.videolist.movies_db.MoviesEntity
import ollu.dp.ua.videolist.movies_db.MoviesResult
import ollu.dp.ua.videolist.movies_db.fullPosterPath

class Movies(
    val page: Int = 1,
    val total_results: Int = 0,
    val total_pages: Int = 0,
    val results: List<Movie> = emptyList()
)

data class Movie(val id: Int = 0, val title: String = "", val poster: String = "") {
    override fun equals(other: Any?): Boolean {
        if (other !is Movie) return false
        if (id != other.id) return false
        if (poster != other.poster) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + poster.hashCode()
        return result
    }
}

data class MovieDetails(
    val id: Int,
    val title: String,
    val poster: String?,
    val summary: String,
    val cast: String,
    val director: String,
    val year: Int,
    val trailer: String
)

fun MoviesResult.toMovies(): Movies {
    return Movies(page, total_results, total_pages, results.map { it.toMovie() })
}

fun MoviesEntity.toMovie(): Movie {
    return Movie(id, title, poster_path?.fullPosterPath ?: "")
}
