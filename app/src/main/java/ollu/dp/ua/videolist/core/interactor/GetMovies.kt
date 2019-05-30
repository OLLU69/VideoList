package ollu.dp.ua.videolist.core.interactor

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import kotlinx.coroutines.*
import ollu.dp.ua.videolist.features.movies.Movie
import ollu.dp.ua.videolist.features.movies.Movies
import ollu.dp.ua.videolist.features.movies.MoviesRepository
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

private const val PAGE_SIZE = 20

@Singleton
class GetMovies
@Inject constructor(private val repository: MoviesRepository) : UseCase<List<Movie>>() {
    private lateinit var movies: LiveData<PagedList<Movie>>
    private var searchQuery: String = ""

    fun getVideos(onSuccess: (LiveData<PagedList<Movie>>) -> Unit, onError: (String) -> Unit) {
        try {
            onSuccess(getPagedList())
        } catch (ex: Exception) {
            onError(ex.message ?: UNKNOWN_ERROR)
        }
    }

    private fun getPagedList(): LiveData<PagedList<Movie>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_SIZE)
            .build()
        movies = LivePagedListBuilder<Int, Movie>(
            MoviesDataSourceFactory(repository) { searchQuery },
            config
        )
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
        return movies
    }

    fun filterVideos(query: String?) {
        DelayJob.queryJob(500) {
            this.searchQuery = query ?: ""
            movies.value?.dataSource?.invalidate()
        }
    }
}

object DelayJob {
    private var launchJob: Job? = null

    private const val TAG = "DelayJob"

    fun queryJob(period: Long, job: () -> Unit) {
        if (launchJob != null) launchJob?.cancel()
        launchJob = GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Job launched")
            delay(period)
            withContext(Dispatchers.Main) {
                Log.d(TAG, "Job started")
                launchJob = null
                job()
            }
        }
    }
}

class MoviesDataSourceFactory(
    private val repository: MoviesRepository,
    val getQuery: () -> String
) : DataSource.Factory<Int, Movie>() {
    override fun create(): DataSource<Int, Movie> {
        return VideosDataSource(repository, getQuery())
    }
}

class VideosDataSource @Inject constructor(
    private val repository: MoviesRepository,
    private val query: String
) : PositionalDataSource<Movie>() {
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Movie>) {
        Log.d(TAG, "loadInitial(${params.requestedStartPosition}, ${params.requestedLoadSize})")
        var startPage = getStartPage(params.requestedStartPosition)
        val pageCount = getPageCount(params.requestedStartPosition, params.requestedLoadSize)
        var totalCount = 0
        var totalPages = 0
        var pages = getPages(startPage, pageCount) { totalPagesCount, totalSize ->
            totalCount = totalSize
            totalPages = totalPagesCount
        }
        if (pages.isEmpty()) {
            if (totalCount == 0) {
                startPage = 1
            } else {
                startPage = getStartPage(totalCount - 1)
                pages = getPages(totalPages, 1)
            }
        }
        Log.d(TAG, "onResult(${pages.size}, ${(startPage - 1) * PAGE_SIZE}, $totalCount")
        callback.onResult(pages, (startPage - 1) * PAGE_SIZE, totalCount)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Movie>) {
        Log.d(TAG, "loadRange(${params.startPosition}, ${params.loadSize})")
        val startPage = getStartPage(params.startPosition)
        val pageCount = getPageCount(params.startPosition, params.loadSize)
        var pages = getPages(startPage, pageCount)
        val shift = params.startPosition % PAGE_SIZE
        pages = if (pages.size <= shift) emptyList() else pages
        Log.d(TAG, "pages.size = ${pages.size}")
        callback.onResult(pages)
    }

    private fun getStartPage(startPosition: Int): Int = startPosition / PAGE_SIZE + 1

    private fun getPageCount(startPosition: Int, size: Int) = (startPosition % PAGE_SIZE + size - 1) / PAGE_SIZE + 1

    private fun getPages(startPage: Int, pageCount: Int, pageParams: ((Int, Int) -> Unit)? = null): List<Movie> {
        return try {
            val movies = mutableListOf<Movie>()
            val getResult: (Int) -> Movies =
                if (query.isBlank()) { page -> repository.movies(page) }
                else { page -> repository.searchMovies(page, query) }
            var paramsFun = pageParams
            (startPage until startPage + pageCount).forEach {
                getResult(it).apply {
                    paramsFun?.apply { invoke(total_pages, total_results); paramsFun = null }
                    movies += results
                }
            }
            movies
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }

    companion object {
        private const val TAG = "GetMovies"
    }
}
