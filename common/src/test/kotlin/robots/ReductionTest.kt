package robots

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

open class ReductionTest {
    @Test
    fun reduce_nop() {
        nop.reducesTo(null, null)
    }
    
    @Test
    fun reduce_single_action() {
        Seq(a).reducesTo(a, nop)
    }
    
    @Test
    fun reduce_sequence_of_actions() {
        Seq(a, b).reducesTo(a, Seq(b))
        Seq(a, b, c).reducesTo(a, Seq(b, c))
    }
    
    @Test
    fun reduce_sequence_starting_with_repeat() {
        Seq(Repeat(10, a, b), c).reducesTo(null, Seq(Seq(a, b), Repeat(9, a, b), c))
        Seq(Repeat(10, a), b).reducesTo(null, Seq(Seq(a), Repeat(9, a), b))
        Seq(Repeat(1, a, b), c).reducesTo(null, Seq(Seq(a, b), c))
        Seq(Repeat(1, a), b).reducesTo(null, Seq(Seq(a), b))
    }
    
    @Test
    fun reduce_sequence_starting_with_empty_sequence() {
        Seq(Seq(), a).reducesTo(null, Seq(a))
    }
    
    @Test
    fun reduce_sequence_starting_with_nonempty_sequence() {
        Seq(Seq(a, b), Seq(c, d)).reducesTo(null, Seq(a, Seq(b), Seq(c, d)))
    }
    
    @Test
    fun reduce_sequence_starting_with_single_element_sequence() {
        Seq(Seq(a), Seq(b, c)).reducesTo(null, Seq(a, Seq(b, c)))
    }
    
    @Test
    fun reduce_sequence_starting_with_sequences_starting_with_a_repeat() {
        Seq(Seq(Repeat(10, Seq(a)), b), Seq(c, d)).reducesTo(null, Seq(Repeat(10, Seq(a)), Seq(b), Seq(c, d)))
    }
    
    private fun Seq.reducesTo(expectedAction: Action?, expectedNext: Seq?) {
        this reducesTo Reduction(this, expectedAction, expectedNext)
    }
    
    @Test
    fun reduce_to_action_always_returns_action_with_next_state() {
        var p: Seq? = Seq(Seq(Repeat(10, Seq(a)), b), Seq(c, d))
        while (p != null) {
            val (_, action, next) = p.reduceToAction()
            assertTrue(next == null || action != null, "next: ${next}, action: ${action}")
            
            p = next
        }
    }
    
    private infix fun Seq.reducesTo(expected: Reduction) {
        val reduced = reduce()
        if (reduced != expected) {
            fail("reduction of $this\nexpected: $expected\nactual:   $reduced")
        }
    }
    
    private val a = Action("a")
    private val b = Action("b")
    private val c = Action("c")
    private val d = Action("d")
    private val e = Action("e")
}
