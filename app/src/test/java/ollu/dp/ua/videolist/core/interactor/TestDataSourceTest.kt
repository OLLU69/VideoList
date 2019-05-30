package ollu.dp.ua.videolist.core.interactor

import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors

//private const val PAGE_SIZE = 4
private var pageSIZE = 4
private const val DELAY = 1000L
private var totalSize: Int = 0

class TestDataSourceTest {

    private lateinit var repository: TestRepository

    private lateinit var source: TestDataSource

    @Before
    fun setUp() {
        val data = listOf(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
        )
        totalSize = data.size
        repository = TestRepository(data)
        source = TestDataSource(repository)
    }

    @Test
    fun getFrom0PositionTest() {
        pagedPagedList(source).apply {
            println("getFrom0PositionTest-------------------------------------")
            assertEquals(12, size)
            printList()
            println(loadedCount)
            assertEquals("12", this[11])
            assertEquals(12, size)
            loadAround(11)
            Thread.sleep(DELAY)
            assertEquals("16", this[15])
            assertEquals(16, size)
            printList()

            loadAround(15)
            Thread.sleep(DELAY)
            assertEquals("20", this[19])
            assertEquals(20, size)
            printList()
        }
    }

    @Test
    fun getFromInsidePositionPositionTest() {
        pagedPagedList(source, 1).apply {
            println("getFromInsidePositionPositionTest-------------------------------------")
            assertEquals(16, size)
            printList()
            println(loadedCount)
            assertEquals("13", this[12])
            assertEquals(16, size)
            loadAround(15)
            Thread.sleep(DELAY)
            assertEquals("20", this[19])
            assertEquals(20, size)
            printList()
            loadAround(0)
            Thread.sleep(DELAY)
            printList()
        }
    }

    @Test
    fun getFromNearToEndRangePositionTest() {

        pagedPagedList(source, 18).apply {
            println("getFromNearToEndRangePositionTest-------------------------------------")
            assertEquals(4, size)
            printList()
            repeat(4) {
                loadAround(0)
                Thread.sleep(DELAY)
                printList()
            }
        }
    }

    @Test
    fun getFromOutOfRangePositionTest() {

        pagedPagedList(source, 21).apply {
            println("getFromOutOfRangePositionTest-------------------------------------")
            assertEquals(4, size)
            printList()
            loadAround(0)
            Thread.sleep(DELAY)
            printList()
            loadAround(0)
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
    fun getRangePositionTest() {
        pageSIZE = 20
        repository.data = List(34) { (it + 1).toString() }
        totalSize = repository.data.size
        pagedPagedList(source, 10).apply {
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
        repository.data = List(34) { (it + 1).toString() }
        totalSize = repository.data.size
        pagedPagedList(source, 31).apply {
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

    private fun PagedList<String>.printList() {
        println("size $size")
        println("loadedCount $loadedCount")
        forEach { print("$it, ") }
        println()
    }

    private fun pagedPagedList(
        source: TestDataSource,
        startPosition: Int = 0
    ): PagedList<String> {

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(pageSIZE)
            .build()
        return PagedList.Builder(source, config)
            .setInitialKey(startPosition)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor(Executors.newSingleThreadExecutor())
            .build()
    }
}

class TestRepository(var data: List<String>) {
    fun movies(page: Int): List<String> {
        assertTrue(1 <= page)
        return data.getPage(page - 1)
    }

}

private fun <E> List<E>.getPage(page: Int): List<E> {
    var start = page * pageSIZE
    var end = start + pageSIZE
    if (end > size) end = size
    if (start > end) start = end
    return subList(start, end)
}

class TestDataSource(
    private val repository: TestRepository
) : PositionalDataSource<String>() {
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<String>) {
        println("loadInitial(${params.requestedStartPosition}, ${params.requestedLoadSize})")
        var startPage = getStartPage(params.requestedStartPosition)
        val pageCount = getPageCount(params.requestedStartPosition, params.requestedLoadSize)
        var pages = getPages(startPage, pageCount)
        if (pages.isEmpty()) {
            if (totalSize == 0) {
                callback.onResult(pages, 0, 0)
                return
            } else {
                startPage = getStartPage(totalSize - 1)
                pages = getPages(startPage, 1)
            }
        }
        println("onResult(${pages.size}, ${(startPage - 1) * pageSIZE}, $totalSize)")
        callback.onResult(pages, (startPage - 1) * pageSIZE, totalSize)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<String>) {
        println("loadRange(${params.startPosition}, ${params.loadSize})")
        val startPage = getStartPage(params.startPosition)
        val pageCount = getPageCount(params.startPosition, params.loadSize)
        var pages = getPages(startPage, pageCount)
        val shift = params.startPosition % pageSIZE
        pages = if (pages.size <= shift) emptyList() else pages
        println("onResult(${pages.size})")
        callback.onResult(pages)
    }

    private fun getStartPage(startPosition: Int): Int = startPosition / pageSIZE + 1

    private fun getPageCount(startPosition: Int, size: Int) = (startPosition % pageSIZE + size - 1) / pageSIZE + 1

    private fun getPages(startPage: Int, pageCount: Int): List<String> {
        return try {
            val movies = mutableListOf<String>()
            (startPage until startPage + pageCount).forEach { page ->
                movies += repository.movies(page)
            }
            movies
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }
}
