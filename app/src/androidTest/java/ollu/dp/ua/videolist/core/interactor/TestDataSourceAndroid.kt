package ollu.dp.ua.videolist.core.interactor

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.test.runner.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import ollu.dp.ua.videolist.features.movies.Movie
import ollu.dp.ua.videolist.features.movies.MovieDetails
import ollu.dp.ua.videolist.features.movies.Movies
import ollu.dp.ua.videolist.features.movies.MoviesRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val DELAY = 1000L
private const val PAGE_SIZE = 20
private var totalSize: Int = 0

@RunWith(AndroidJUnit4::class)
class TestDataSourceTestAndroid {

    private lateinit var repository: TestRepository
    private lateinit var getMovies: GetMovies

    @Before
    fun setUp() {
        val data = listOf(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "20",
            "31", "32", "33", "34", "35", "36", "37", "38", "39", "30",
            "41", "42", "43", "44", "45", "46", "47", "48", "49", "40"
        )
        totalSize = data.size
        repository = TestRepository(data)
    }

    @Test
    fun getRangePositionTest() {
        val dt = List(34) { (it + 1).toString() }
        repository = TestRepository(dt)
        totalSize = repository.data.size
        pagedPagedList().apply {
            println("getRangePositionTest-------------------------------------")
            assertEquals(34, size)
            printList()
            loadAround(33)
            Thread.sleep(DELAY)
            printList()
            loadAround(33)
            Thread.sleep(DELAY)
            printList()
            loadAround(0)
            Thread.sleep(DELAY)
            printList()
            loadAround(0)
            Thread.sleep(DELAY)
            printList()
        }
    }

    @Test
    fun getRangeWithSmallPagesPositionTest() {
        repository = TestRepository(List(34) { (it + 1).toString() })
        totalSize = repository.data.size
        pagedPagedList().apply {
            println("getRangeWithSmallPagesPositionTest-------------------------------------")
            assertEquals(6, size)
            printList()
            loadAround(5)
            Thread.sleep(DELAY)
            printList()
            repeat(8) {
                loadAround(0)
                Thread.sleep(DELAY)
                printList()
            }
        }
    }

    private fun PagedList<Movie>.printList() {
        println("size $size")
        println("loadedCount $loadedCount")
        forEach { print("${it.title}, ") }
        println()
    }


    private fun pagedPagedList(): PagedList<Movie> {

        var result: LiveData<PagedList<Movie>>?
        val chanel = Channel<PagedList<Movie>>()
        getMovies = GetMovies(repository)
        getMovies.filterVideos("")
        getMovies.getVideos({
            result = it
            runBlocking(Dispatchers.Main) {
                result?.observeForever {
                    runBlocking(Dispatchers.IO) {
                        chanel.send(it)
                    }
                }
            }
        }, {
            fail()
        })

        return runBlocking {
            chanel.receive()
        }
    }

    class TestRepository(data: List<String>) : MoviesRepository {
        val data = data.map { Movie(0, it) }
        override fun movies(page: Int): Movies {
            assertTrue(1 <= page)
            val results = data.getPage(page - 1)
            return Movies(page, data.size, data.size / PAGE_SIZE + 1, results)
        }

        override fun searchMovies(page: Int, query: String): Movies {
            assertTrue(1 <= page)
            val results = data.filter { it.title.contains(query) }.getPage(page - 1)
            return Movies(page, data.size, data.size / PAGE_SIZE + 1, results)
        }

        override fun movieDetails(movieId: Int): MovieDetails {
            return MovieDetails(0, "", "", "", "", "", 0, "")
        }
    }
}


private fun <E> List<E>.getPage(page: Int): List<E> {
    var start = page * PAGE_SIZE
    var end = start + PAGE_SIZE
    if (end > size) end = size
    if (start > end) start = end
    return subList(start, end)
}

