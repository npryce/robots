package robots

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class ASTPathTest {
    private val a = Action("a")
    private val b = Action("b")
    private val c = Action("c")
    private val d = Action("d")
    private val e = Action("e")
    
    private val program = Seq(a, b, Repeat(3, c, d), e)
    
    val x = Action("x")
    
    @Test
    fun get_at_path() {
        assertEquals(a, program[0, 0])
        assertEquals(b, program[0, 1])
        assertEquals(Repeat(3, c, d), program[0, 2])
        assertEquals(c, program[0, 2, 0, 0])
        assertEquals(d, program[0, 2, 0, 1])
        assertEquals(e, program[0, 3])
    }
    
    @Test
    fun paths_have_a_total_order() {
        assertTrue(pathOf(0, 0) < pathOf(1, 0))
        assertTrue(pathOf(0, 0) < pathOf(0, 1))
        assertTrue(pathOf(0, 1) < pathOf(1, 1))
        assertTrue(pathOf(0, 1) < pathOf(0, 1, 0, 0))
        assertTrue(pathOf(0, 1, 0, 0) < pathOf(0, 1, 1, 0))
        assertTrue(pathOf(0, 1, 0, 0) < pathOf(0, 1, 0, 1))
        assertTrue(pathOf(0, 1, 0, 1) < pathOf(0, 1, 1, 1))
    }
    
    @Test
    fun reports_if_path_contains_a_subpath() {
        assertTrue(pathOf(0, 0).contains(pathOf(0, 0)))
        assertTrue(pathOf(0, 0).contains(pathOf(0, 0, 1, 0)))
        assertTrue(pathOf(0, 0).contains(pathOf(0, 0, 0, 1)))
        assertTrue(pathOf(0, 0).contains(pathOf(0, 0, 1, 1)))
        assertTrue(!pathOf(0, 0).contains(pathOf(1, 0)))
        assertTrue(!pathOf(0, 0).contains(pathOf(0, 1)))
    }
    
    @Test
    fun can_replace_top_level_program_element() {
        assertEquals(actual = program.replaceAt(pathOf(0, 1), x), expected = Seq(a, x, Repeat(3, c, d), e))
    }
    
    @Test
    fun can_replace_top_level_subtree() {
        assertEquals(actual = program.replaceAt(pathOf(0, 2), x), expected = Seq(a, b, x, e))
    }
    
    @Test
    fun can_replace_element_of_subtree() {
        assertEquals(actual = program.replaceAt(pathOf(0, 2, 0, 1), x), expected = Seq(a, b, Repeat(3, c, x), e))
    }
    
    @Test
    fun can_replace_element_of_deep_subtree() {
        val program = Seq(a, Repeat(2, b, Repeat(3, c, Repeat(4, d, e))))
        assertEquals(actual = program.replaceAt(pathOf(0, 1, 0, 1, 0, 1), x),
            expected = Seq(a, Repeat(2, b, Repeat(3, c, x))))
        assertEquals(actual = program.replaceAt(pathOf(0, 1, 0, 1, 0, 1, 0, 0), x),
            expected = Seq(a, Repeat(2, b, Repeat(3, c, Repeat(4, x, e)))))
    }
    
    @Test
    fun can_remove_top_level_element() {
        assertEquals(actual = program.removeAt(pathOf(0, 1)), expected = Seq(a, Repeat(3, c, d), e))
    }
    
    @Test
    fun can_remove_top_level_subtree() {
        assertEquals(actual = program.removeAt(pathOf(0, 2)), expected = Seq(a, b, e))
    }
    
    @Test
    fun can_remove_element_of_subtree() {
        assertEquals(actual = program.removeAt(pathOf(0, 2, 0, 1)), expected = Seq(a, b, Repeat(3, c), e))
    }
    
    @Test
    fun can_insert_before_program_element() {
        assertEquals(actual = program.insertBefore(pathOf(0, 1), x), expected = Seq(a, x, b, Repeat(3, c, d), e))
    }
    
    @Test
    fun can_insert_before_top_level_subtree() {
        assertEquals(actual = program.insertBefore(pathOf(0, 2), x), expected = Seq(a, b, x, Repeat(3, c, d), e))
    }
    
    @Test
    fun can_insert_before_element_of_subtree() {
        assertEquals(actual = program.insertBefore(pathOf(0, 2, 0, 1), x), expected = Seq(a, b, Repeat(3, c, x, d), e))
    }
    
    @Test
    fun can_insert_after_top_level_element() {
        assertEquals(actual = program.insertAfter(pathOf(0, 1), x), expected = Seq(a, b, x, Repeat(3, c, d), e))
    }
    
    @Test
    fun can_insert_after_top_level_subtree() {
        assertEquals(actual = program.insertAfter(pathOf(0, 2), x), expected = Seq(a, b, Repeat(3, c, d), x, e))
    }
    
    @Test
    fun can_insert_after_element_of_subtree() {
        assertEquals(actual = program.insertAfter(pathOf(0, 2, 0, 1), x), expected = Seq(a, b, Repeat(3, c, d, x), e))
    }
    
    open fun assertEquals(actual: Seq, expected: Seq) {
        kotlin.test.assertEquals(actual, expected)
    }
}

