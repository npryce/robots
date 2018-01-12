package robots.ui

import robots.Action
import robots.Repeat
import robots.Seq
import robots.UndoRedoStack
import robots.reduce
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


open class ExecutionCostTest {
    val a = Action("a")
    val b = Action("b")
    val c = Action("c")
    val e = Action("e")
    
    
    @Test
    fun initial_state_cost_zero() {
        val running = Editing(UndoRedoStack(Seq(a, b, c, e))).startRunning()
        
        assertEquals(expected = 0, actual = running.trace.cost())
    }
    
    @Test
    fun each_action_costs_one_unit() {
        val running = Editing(UndoRedoStack(Seq(a, b, c, e))).startRunning()
        
        assertEquals(expected = 1, actual = running.step().trace.cost(),
            message="after one step")
        assertEquals(expected = 2, actual = running.step().step().trace.cost(),
            message = "after two steps")
        assertEquals(expected = 3, actual = running.step().step().step().trace.cost(),
            message = "after three steps")
        assertEquals(expected = 4, actual = running.step().step().step().step().trace.cost(),
            message = "after four steps")
    }
    
    @Test
    fun reduction_steps_that_produce_no_action_have_no_cost() {
        val running = Editing(UndoRedoStack(Seq(Repeat(3, b)))).startRunning()
        val afterStep = running.step(Seq::reduce)
        
        assertNull(afterStep.trace.current.action)
        assertEquals(expected = 0, actual = afterStep.trace.cost())
    }
}