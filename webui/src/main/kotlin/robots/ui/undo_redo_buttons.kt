package robots.ui

import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import react.RBuilder
import react.dom.button
import robots.Seq
import robots.UndoRedoStack
import robots.canRedo
import robots.canUndo
import robots.redo
import robots.undo

fun RBuilder.undoRedoButtons(undoStack: UndoRedoStack<Seq>, disabled: Boolean = false, update: (UndoRedoStack<Seq>) -> Unit) {
    button(classes = backwards) {
        attrs.title = "Undo"
        attrs.onClickFunction = { update(undoStack.undo()) }
        attrs.disabled = disabled || !undoStack.canUndo()
        +"Undo"
    }
    
    button(classes = forwards) {
        attrs.title = "Redo"
        attrs.onClickFunction = { update(undoStack.redo()) }
        attrs.disabled = disabled || !undoStack.canRedo()
        +"Redo"
    }
}
