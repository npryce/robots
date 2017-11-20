package robots

import kotlin.test.Test
import kotlin.test.assertEquals

class PListZipperTest {
    @Test
    fun traversing() {
        val xs = pListOf(1,2,3,4,5,6)
        val x = xs.zipper().next()?.next()?.current
        
        assertEquals(actual = x, expected = 3)
    }
    
    @Test
    fun modifying() {
        val xs = pListOf(1,2,3,4,5,6)
        val munged = xs.zipper().next()?.next()?.remove()?.replaceWith(99)?.toPList()
        
        assertEquals(actual = munged, expected = pListOf(1, 2, 99, 5, 6))
    }
}