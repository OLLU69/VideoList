package ollu.dp.ua.videolist

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private var itemsPerPage = 3
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    private fun getListPosition(position: Int) = position % itemsPerPage

    @Test
    fun positionTest() {
        assertEquals(0, getListPosition(0))
        assertEquals(1, getListPosition(1))
        assertEquals(2, getListPosition(2))
        assertEquals(0, getListPosition(3))
        assertEquals(1, getListPosition(4))
    }

    @Test
    fun startPosition() {
        Paginating().apply {
            //страницы начинаются с 1
            computeParams(0, 0); assertEquals(1, startPage)
            computeParams(1, 0); assertEquals(1, startPage)
            computeParams(2, 0); assertEquals(1, startPage)
            computeParams(3, 0); assertEquals(2, startPage)
            computeParams(4, 0); assertEquals(2, startPage)
            computeParams(5, 0); assertEquals(2, startPage)
            computeParams(6, 0); assertEquals(3, startPage)
            computeParams(7, 0); assertEquals(3, startPage)
            computeParams(8, 0); assertEquals(3, startPage)
        }
    }

    @Test
    fun pageCountTest() {
        Paginating().apply {
            computeParams(0, 3); assertEquals(1, pageCount);assertEquals(1, startPage)
            computeParams(0, 6); assertEquals(2, pageCount)
            computeParams(0, 9); assertEquals(3, pageCount)
            computeParams(0, 12); assertEquals(4, pageCount)

            computeParams(1, 3); assertEquals(2, pageCount); assertEquals(1, startPage)
            computeParams(2, 3); assertEquals(2, pageCount); assertEquals(1, startPage)
            computeParams(3, 3); assertEquals(1, pageCount); assertEquals(2, startPage)

            computeParams(1, 6); assertEquals(3, pageCount); assertEquals(1, startPage)
            computeParams(2, 6); assertEquals(3, pageCount); assertEquals(1, startPage)
            computeParams(3, 6); assertEquals(2, pageCount); assertEquals(2, startPage)
        }
    }
}

class Paginating {
    private var startPosition: Int = 0
    private var size: Int = 0
    val startPage: Int
        get() = startPosition / PAGE_SIZE + 1
    val pageCount: Int
        get() = (startPosition % PAGE_SIZE + size - 1) / PAGE_SIZE + 1

    fun computeParams(startPosition: Int, size: Int) {
        this.startPosition = startPosition
        this.size = size
    }

}

private const val PAGE_SIZE = 3
