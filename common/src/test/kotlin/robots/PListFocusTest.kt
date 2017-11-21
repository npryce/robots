package robots

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

open class PListFocusTest {
    @Test
    fun traversing() {
        val xs = pListOf(1,2,3,4,5,6)
        val x = xs.zipper()?.next()?.next()?.current
        
        assertEquals(actual = x, expected = 3)
    }
    
    @Test
    fun modifying() {
        val xs = pListOf(1,2,3,4,5,6)
        val munged = xs.zipper()?.next()?.next()?.remove()?.replaceWith(99)?.toPList()
        
        assertEquals(actual = munged, expected = pListOf(1, 2, 99, 5, 6))
    }
    
    @Test
    fun remove_last_shifts_zipper_backwards() {
        val xs = pListOf(1, 2, 3, 4)
        
        val afterRemoval = xs.zipper()?.next()?.next()?.next()?.remove() ?: fail("no zipper")
        
        assertEquals(actual = afterRemoval.toPList(), expected = pListOf(1, 2, 3))
        assertEquals(actual = 3, expected = afterRemoval.current)
    }
    
    @Test
    fun remove_first_shifts_zipper_forward() {
        val xs = pListOf(1, 2, 3, 4)
        
        val afterRemoval = xs.zipper()?.remove() ?: fail("no zipper")
        
        assertEquals(actual = afterRemoval.toPList(), expected = pListOf(2, 3, 4))
        assertEquals(actual = 2, expected = afterRemoval.current)
    }
    
    @Test
    fun remove_middle_shifts_zipper_forwards() {
        val xs = pListOf(1, 2, 3, 4)
        
        val afterRemoval = xs.zipper()?.next()?.remove() ?: fail("no zipper")
        
        assertEquals(actual = afterRemoval.toPList(), expected = pListOf(1, 3, 4))
        assertEquals(actual = 3, expected = afterRemoval.current)
    }
}