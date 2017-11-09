package robots

import org.junit.Assert.fail
import org.junit.Test

class ReductionTests {
    private val a = Action("a")
    private val b = Action("b")
    private val c = Action("c")
    private val d = Action("d")
    
    @Test
    fun `reduce no-op`() {
        Seq(Empty) reducesTo Reduction(null, null)
    }
    
    @Test
    fun `reduce action`() {
        a reducesTo Reduction(a, null)
        b reducesTo Reduction(b, null)
    }
    
    @Test
    fun `reduce repeat`() {
        Repeat(10, a) reducesTo Reduction(null, Seq(a, Repeat(9, a)))
        Repeat(1, a) reducesTo Reduction(null, a)
    }
    
    @Test
    fun `reduce linear sequence of actions`() {
        Seq(a, b) reducesTo Reduction(a, Seq(b))
        Seq(a, b, c) reducesTo Reduction(a, Seq(b, c))
    }
    
    @Test
    fun `reduce sequence starting with repeat`() {
        Seq(Repeat(10, a), b) reducesTo Reduction(null, Seq(Seq(a, Repeat(9, a)), b))
    }
    
    @Test
    fun `reduce sequence of sequences`() {
        Seq(Seq(a,b), Seq(c, d)) reducesTo Reduction(a, Seq(Seq(b), Seq(c,d)))
    }
    
    @Test
    fun `reduce sequence of sequences with a repeat`() {
        Seq(Seq(Repeat(10,a), b), Seq(c, d)) reducesTo Reduction(null, Seq(Seq(Seq(a, Repeat(9, a)), b), Seq(c, d)))
    }
    
    private infix fun AST.reducesTo(expected: Reduction) {
        val reduced = reduce()
        if (reduced != expected) {
            fail("reduction of ${this.toCompactString()}\nexpected: ${expected.toCompactString()}\nactual:   ${reduced.toCompactString()}")
        }
    }
    
    private fun Reduction.toCompactString() = "Reduction(action=${action?.toCompactString()}, future=${future?.toCompactString()})"
}
