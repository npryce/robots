package robots

import kotlin.test.Test
import kotlin.test.assertEquals


open class SplitAfterTest {
    @Test
    fun splits_iterable_after_matching_elements() {
        val xs = listOf(1,2,3,4,5,6,7,8)
        val chunks = xs.splitAfter { (it % 3) == 0 }
        
        assertEquals(actual = chunks, expected = listOf(listOf(1,2,3), listOf(4,5,6), listOf(7,8)))
    }
    
    @Test
    fun splitting_with_full_final_chunk() {
        val xs = listOf(1,2,3,4,5,6)
        val chunks = xs.splitAfter { (it % 3) == 0 }
        
        assertEquals(actual = chunks, expected = listOf(listOf(1,2,3), listOf(4,5,6)))
    }
    
    @Test
    fun splitting_an_empty_iterable_gives_an_empty_list() {
        val xs = emptyList<Int>()
        val chunks = xs.splitAfter { (it % 3) == 0 }
        
        assertEquals(actual = chunks, expected = emptyList())
    }
}