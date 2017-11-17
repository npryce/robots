package robots

import org.junit.Assert.fail
import org.junit.Test

class ReductionTests {
    @Test
    fun `reduce nop`() {
        nop reducesTo Reduction(null, null)
    }
    
    @Test
    fun `reduce single action`() {
        Seq(a) reducesTo Reduction(a, nop)
    }
    
    @Test
    fun `reduce sequence of actions`() {
        Seq(a, b) reducesTo Reduction(a, Seq(b))
        Seq(a, b, c) reducesTo Reduction(a, Seq(b, c))
    }
    
    @Test
    fun `reduce sequence starting with repeat`() {
        Seq(Repeat(10, a, b), c) reducesTo Reduction(null, Seq(Seq(a, b), Repeat(9, a, b), c))
        Seq(Repeat(10, a), b) reducesTo Reduction(null, Seq(Seq(a), Repeat(9, a), b))
        Seq(Repeat(1, a, b), c) reducesTo Reduction(null, Seq(Seq(a, b), c))
        Seq(Repeat(1, a), b) reducesTo Reduction(null, Seq(Seq(a), b))
    }
    
    @Test
    fun `reduce sequence starting with empty sequence`() {
        Seq(Seq(), a) reducesTo Reduction(null, Seq(a))
    }
    
    @Test
    fun `reduce sequence starting with non-empty sequence`() {
        Seq(Seq(a, b), Seq(c, d)) reducesTo Reduction(null, Seq(a, Seq(b), Seq(c, d)))
    }
    
    @Test
    fun `reduce sequence starting with single-element sequence`() {
        Seq(Seq(a), Seq(b, c)) reducesTo Reduction(null, Seq(a, Seq(b, c)))
    }
    
    @Test
    fun `reduce sequence starting with sequences starting with a repeat`() {
        Seq(Seq(Repeat(10, Seq(a)), b), Seq(c, d)) reducesTo Reduction(null, Seq(Repeat(10, Seq(a)), Seq(b), Seq(c, d)))
    }
    
    private infix fun Seq.reducesTo(expected: Reduction) {
        val reduced = reduce()
        if (reduced != expected) {
            fail("reduction of ${this.toCompactString()}\nexpected: ${expected.toCompactString()}\nactual:   ${reduced.toCompactString()}")
        }
    }
    
    private fun Reduction.toCompactString() = "Reduction(action=${action?.name}, future=${future?.toCompactString()})"
}
