package robots.ui

import robots.Reduction
import robots.Seq
import robots.UndoRedoStack
import robots.havingDone
import robots.nop
import robots.reduceToAction


sealed class GameState {
    abstract val source: UndoRedoStack<Seq>
}

data class Editing(override val source: UndoRedoStack<Seq>) : GameState()
data class Running(override val source: UndoRedoStack<Seq>, val trace: UndoRedoStack<Reduction>?) : GameState()

fun initialGameState(): GameState =
    Editing(UndoRedoStack(nop))

fun GameState.isRunning() =
    this is Running

fun GameState.startRunning() = when (this) {
    is Editing -> Running(source, null)
    is Running -> this
}

fun Running.hasFinished() =
    trace != null && trace.current.next == nop

fun Running.stopRunning() =
    Editing(source)

fun Running.step(): Running {
    val currentState = if (trace == null) source.current else trace.current.next
    
    return if (currentState == nop) {
        this
    }
    else {
        val nextState = currentState.reduceToAction()
        val newTrace = trace?.havingDone(nextState) ?: UndoRedoStack(nextState)
        copy(trace = newTrace)
    }
}

fun Running.canStepBackward() =
    trace != null && trace.hasPrev()

fun Running.canStepForward() =
    trace != null && trace.hasNext()
