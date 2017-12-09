package robots

import kotlin.test.Test
import kotlin.test.assertEquals


open class TakeUntilTest {
    @Test
    fun takes_up_to_and_including_matching_element() {
        assertEquals(actual = sequenceOf(1, 2, 3, 4, 5).takeUntil { it == 3 }.toList(),
            expected = listOf(1, 2, 3))
    }
    
    @Test
    fun handles_empty_sequence() {
        assertEquals(actual = emptySequence<Int>().takeUntil { it == 3 }.toList(),
            expected = emptyList<Int>())
    }
    
    @Test
    fun handles_last_element_being_separator() {
        assertEquals(actual = sequenceOf(1,2,3,4).takeUntil { it == 4 }.toList(),
            expected = listOf(1,2,3,4))
    }
    
    @Test
    fun handles_no_element_being_separator() {
        assertEquals(actual = sequenceOf(1,2,3,4).takeUntil { it == -1 }.toList(),
            expected = listOf(1,2,3,4))
    }
}