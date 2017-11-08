package robots

import org.junit.Assert.assertEquals
import org.junit.Test

class ReductionTest {
    private val a = Action("a")
    private val b = Action("b")
    private val c = Action("c")
    private val d = Action("d")
    private val e = Action("e")
    
    
    @Test
    fun `reduce action`() {
        a reducesTo Reduction(a, null)
        b reducesTo Reduction(b, null)
    }
    
    @Test
    fun `reduce repeat`() {
        Repeat(10, a) reducesTo Reduction(a, Repeat(9, a))
        Repeat(1, a) reducesTo Reduction(a, null)
    }
    
    @Test
    fun `reduce linear sequence of actions`() {
        Seq(a, b) reducesTo Reduction(a, Seq(b))
        Seq(a, b, c) reducesTo Reduction(a, Seq(b, c))
    }
    
    @Test
    fun `reduce sequence starting with repeat`() {
        Seq(Repeat(10, a), b) reducesTo Reduction(a, Seq(Repeat(9, a), b))
    }
    
    @Test
    fun `reduce sequence of sequences`() {
        Seq(Seq(a,b), Seq(c, d)) reducesTo Reduction(a, Seq(Seq(b), Seq(c,d)))
    }
    
    @Test
    fun `reduce sequence of sequences with a repeat`() {
        Seq(Seq(Repeat(10,a), b), Seq(c, d)) reducesTo Reduction(a, Seq(Seq(Repeat(9, a), b), Seq(c, d)))
    }
    
    private infix fun AST.reducesTo(expected: Reduction) {
        val reduced = reduce()
        
        assertEquals("action", expected.action?.toCompactString(), reduced.action?.toCompactString())
        assertEquals("future", expected.future?.toCompactString(), reduced.future?.toCompactString())
    }
}
