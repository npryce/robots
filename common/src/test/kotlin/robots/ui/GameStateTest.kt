package robots.ui

import robots.Action
import robots.Seq
import robots.UndoRedoStack
import robots.canRedo
import robots.canUndo
import robots.nop
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


open class GameStateTest {
    @Test
    fun starts_in_edit_mode_with_empty_program_and_no_edit_history() {
        val game = initialGameState()
        assertEquals(expected = nop, actual = game.source.current)
        assertTrue(!game.source.canUndo())
        assertTrue(!game.source.canRedo())
    }
    
    private val a = Action("a")
    private val b = Action("b")
    
    @Test
    fun when_starts_running_trace_has_source_as_next_graph_to_reduce() {
        val editing = Editing(UndoRedoStack(Seq(a, b)))
        val running = editing.startRunning()
        
        assertEquals(editing.source, running.source)
        assertEquals(running.source.current, running.trace.current.next)
    }
    
    @Test
    fun when_starts_running_cannot_undo_or_redo_step() {
        val editing = Editing(UndoRedoStack(Seq(a, b)))
        val running = editing.startRunning()
        
        assertFalse(running.canRedoStep())
        assertFalse(running.canUndoStep())
    }
    
    @Test
    fun can_step_to_end_of_program() {
        val running = Editing(UndoRedoStack(Seq(a, b))).startRunning()
        assertFalse(running.hasFinished(), "start")
        assertFalse(running.step().hasFinished(), "one step")
        assertTrue(running.step().step().hasFinished(), "two steps")
    }
    
    @Test
    fun can_undo_execution_steps() {
        val running = Editing(UndoRedoStack(Seq(a, b))).startRunning()
        
        assertEquals(actual = running.step().undoStep().trace.current, expected = running.trace.current)
        assertEquals(actual = running.step().step().undoStep().trace.current, expected = running.step().trace.current)
        assertEquals(actual = running.step().step().undoStep().undoStep().trace.current, expected = running.trace.current)
    }
    
    @Test
    fun can_undo_and_redo_execution_steps() {
        val running = Editing(UndoRedoStack(Seq(a, b))).startRunning()
        
        assertEquals(
            actual = running.step().undoStep().redoStep(),
            expected = running.step())
        assertEquals(
            actual = running.step().step().undoStep().redoStep(),
            expected = running.step().step())
        assertEquals(
            actual = running.step().step().undoStep().undoStep().redoStep().redoStep(),
            expected = running.step().step())
    }
}
