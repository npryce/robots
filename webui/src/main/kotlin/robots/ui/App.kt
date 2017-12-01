package robots.ui

import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.RDOMBuilder
import react.dom.button
import react.dom.div
import react.dom.span
import robots.Seq
import robots.UndoRedoStack
import robots.canRedo
import robots.canUndo
import robots.cost
import robots.havingDone
import robots.redo
import robots.undo


external interface AppProps : RProps {
    var program: Seq
}

external interface AppState : RState {
    var undo: UndoRedoStack<Seq>
}

class App(props: AppProps) : RComponent<AppProps, AppState>(props) {
    init {
        state.undo = UndoRedoStack(props.program)
    }
    
    override fun RBuilder.render() {
        header(state.undo, update = ::updateUndoRedoStack)
        programEditor(state.undo.current, onEdit = ::pushUndoRedoState)
        controlPanel()
    }
    
    private fun pushUndoRedoState(newProgram: Seq) {
        updateUndoRedoStack(state.undo.havingDone(newProgram))
    }
    
    private fun updateUndoRedoStack(newState: UndoRedoStack<Seq>) {
        setState({ it.apply { undo = newState } })
    }
}


fun RBuilder.app(initialProgram: Seq = Seq()) = child(App::class) {
    attrs.program = initialProgram
}

fun RBuilder.header(undoStack: UndoRedoStack<Seq>, update: (UndoRedoStack<Seq>) -> Unit) {
    div("header") {
        span("score") {
            +"Cost: "
            span("cost") { +"£${undoStack.current.cost()}" }
        }
        
        undoRedoButtons(undoStack, update)
    }
}

private fun RDOMBuilder<DIV>.undoRedoButtons(undoStack: UndoRedoStack<Seq>, update: (UndoRedoStack<Seq>) -> Unit) {
    span("edit-controls") {
        button {
            attrs.onClickFunction = { update(undoStack.undo()) }
            attrs.disabled = !undoStack.canUndo()
            +"Undo"
        }
        button {
            attrs.onClickFunction = { update(undoStack.redo()) }
            attrs.disabled = !undoStack.canRedo()
            +"Redo"
        }
    }
}

private fun RBuilder.controlPanel() {
    div("controls") {
        cardStacks()
    }
}
