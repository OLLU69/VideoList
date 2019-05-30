package ollu.dp.ua.videolist.movies_db

data class MoviesResult(val page: Int, val total_results: Int, val total_pages: Int, val results: List<MoviesEntity>)
data class MoviesEntity(val id: Int, val title: String, val poster_path: String?, val overview: String)

data class MovieDetailsEntity(
    val id: Int = 0,
    val title: String = "",
    val poster_path: String? = "",
    val overview: String = "",
    val release_date: String = ""
) {
    val year: Int
        get() = try {
            release_date.substring(0..3).toInt()
        } catch (t: Throwable) {
            0
        }
}

data class Peoples(
    val id: Int,
    val cast: List<Actor>,
    val crew: List<Worker>
)

data class Worker(
    val name: String = "No name",
    val department: String = "No department",
    val job: String = "No job"
)

data class Actor(val cast_id: Int = 0, val name: String = "No name")

data class Trailers(val id: Int = 0, val results: List<Trailer>)
data class Trailer(
    val key: String = "",
    val name: String = "No name",
    val site: String = "YouTube",
    val type: String = "Trailer"
)

val String.fullPosterPath: String
    get() = "http://image.tmdb.org/t/p/w500$this"
val Peoples.director: String
    get() {
        return crew.find { it.job == "Director" }?.name ?: "No name"
    }
val Peoples.actors: String
    get() {
        return cast.joinToString(limit = 5) { it.name }
    }


