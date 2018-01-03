package robots.ui

import robots.Reduction
import robots.Seq
import robots.UndoRedoStack
import robots.havingDone
import robots.next
import robots.nop
import robots.reduceToAction

data class GameState(val editStack: UndoRedoStack<Seq>, val trace: UndoRedoStack<Reduction>?)

fun initialGameState() =
    GameState(editStack = UndoRedoStack(nop), trace = null)

fun GameState.isRunning() =
    trace != null

fun GameState.hasFinished() =
    trace?.current?.next == null

fun GameState.startRunning() =
    copy(trace = UndoRedoStack(editStack.current.reduceToAction()))

fun GameState.stopRunning() =
    copy(trace = null)

fun GameState.step() =
    trace?.run {
        current.next(Seq::reduceToAction)?.let(::havingDone)
    } ?: this
