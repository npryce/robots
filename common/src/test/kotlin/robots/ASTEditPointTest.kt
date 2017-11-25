package robots

import kotlin.test.Test
import kotlin.test.assertEquals

open class ASTEditPointTest {
    val a = Action("a")
    val b = Action("b")
    val c = Action("c")
    val d = Action("d")
    val e = Action("e")
    val x = Action("x")
    
    private val program = Seq(a, b, Repeat(3, c, d), e)
    private val editPoints = program.editPoints()
    
    @Test
    fun can_replace_program_element() {
        assertEquals(actual = editPoints[1].replaceWith(x), expected = Seq(a, x, Repeat(3, c, d), e),
            message = "can replace top level element")
        assertEquals(actual = editPoints[2].replaceWith(x), expected = Seq(a, b, x, e),
            message = "can replace top level subtree")
        
        assertEquals(
            actual = editPoints[2].children()[1].replaceWith(x),
            expected = Seq(a, b, Repeat(3, c, x), e),
            message = "can replace element of subtree")
    }
    
    @Test
    fun can_remove_program_element() {
        assertEquals(actual = editPoints[1].remove(), expected = Seq(a, Repeat(3, c, d), e),
            message = "can remove top level element")
        assertEquals(actual = editPoints[2].remove(), expected = Seq(a, b, e),
            message = "can remove top level subtree")
        
        assertEquals(
            actual = editPoints[2].children()[1].remove(),
            expected = Seq(a, b, Repeat(3, c), e),
            message = "can remove element of subtree")
    }
    
    @Test
    fun can_insert_program_element_before_focus() {
        assertEquals(actual = editPoints[1].insertBefore(x), expected = Seq(a, x, b, Repeat(3, c, d), e),
            message = "can insert before top level element")
        assertEquals(actual = editPoints[2].insertBefore(x), expected = Seq(a, b, x, Repeat(3, c, d), e),
            message = "can insert before top level subtree")
        
        assertEquals(
            actual = editPoints[2].children()[1].insertBefore(x),
            expected = Seq(a, b, Repeat(3, c, x, d), e),
            message = "can insert before element of subtree")
    }
    
    @Test
    fun can_insert_program_element_after_focus() {
        assertEquals(actual = editPoints[1].insertAfter(x), expected = Seq(a, b, x, Repeat(3, c, d), e),
            message = "can insert after top level element")
        assertEquals(actual = editPoints[2].insertAfter(x), expected = Seq(a, b, Repeat(3, c, d), x, e),
            message = "can insert after top level subtree")
        
        assertEquals(
            actual = editPoints[2].children()[1].insertAfter(x),
            expected = Seq(a, b, Repeat(3, c, d, x), e),
            message = "can insert after element of subtree")
    }
    
}

