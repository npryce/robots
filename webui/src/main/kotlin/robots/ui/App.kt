package robots.ui

import browser.SpeechSynthesisUtterance
import browser.speechSynthesis
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button
import react.dom.div
import react.dom.span
import robots.Reduction
import robots.Seq
import robots.UndoRedoStack
import robots.canRedo
import robots.canUndo
import robots.cost
import robots.havingDone
import robots.isRunnable
import robots.nop
import robots.redo
import robots.reduce
import robots.reduceToAction
import robots.undo


external interface AppProps : RProps {
    var program: Seq
    var cards: Deck
}

external interface AppState : RState {
    var undo: UndoRedoStack<Seq>
}

class App(props: AppProps) : RComponent<AppProps, AppState>(props) {
    init {
        state.undo = UndoRedoStack(props.program)
    }
    
    override fun RBuilder.render() {
        header()
        programEditor(props.cards, currentProgram, onEdit = ::pushUndoRedoState)
        controlPanel(props.cards)
    }
    
    fun RBuilder.header() {
        div("header") {
            span("score") {
                +"Cost: "
                span("cost") { +"¢${currentProgram.cost()}" }
            }
            
            controlGroup {
                undoRedoButtons(state.undo, { updateUndoRedoStack(it) })
            }
            
            controlGroup {
                button(classes = "run") {
                    attrs.disabled = !currentProgram.isRunnable()
                    
                    // Dynamic appears to be the only way to access event properties in kotlin-react!
                    attrs.onClickFunction = { ev: dynamic ->
                        if (ev.altKey) runSingleStep() else runNextAction()
                        ev.preventDefault()
                    }
                    
                    +"Run"
                }
            }
        }
    }
    
    private fun pushUndoRedoState(newProgram: Seq) {
        updateUndoRedoStack(state.undo.havingDone(newProgram))
    }
    
    private fun updateUndoRedoStack(newState: UndoRedoStack<Seq>) {
        setState({ it.apply { undo = newState } })
    }
    
    private fun runNextAction() {
        run(Seq::reduceToAction)
    }
    
    private fun runSingleStep() {
        run(Seq::reduce)
    }
    
    fun run(performStep: Seq.() -> Reduction) {
        val (prev, action, next) = currentProgram.performStep()
        
        if (action != null) {
            pushUndoRedoState(prev)
            speechSynthesis.speak(SpeechSynthesisUtterance(action.text).apply {
                onend = {
                    updateUndoRedoStack(state.undo.undo().havingDone(next?:nop))
                }
            })
        }
        else {
            updateUndoRedoStack(state.undo.havingDone(next ?: nop))
        }
    }
    
    private val currentProgram get() = state.undo.current
}

fun RBuilder.app(cards: Deck, initialProgram: Seq = Seq()) = child(App::class) {
    attrs.program = initialProgram
    attrs.cards = cards
}

inline fun RBuilder.controlGroup(contents: RBuilder.() -> Unit) {
    span("control-group", contents)
}


private fun RBuilder.undoRedoButtons(undoStack: UndoRedoStack<Seq>, update: (UndoRedoStack<Seq>) -> Unit) {
    button(classes = "undo") {
        attrs.onClickFunction = { update(undoStack.undo()) }
        attrs.disabled = !undoStack.canUndo()
        +"Undo"
    }
    button(classes = "redo") {
        attrs.onClickFunction = { update(undoStack.redo()) }
        attrs.disabled = !undoStack.canRedo()
        +"Redo"
    }
}

private fun RBuilder.controlPanel(style: Deck) {
    div("controls") {
        cardStacks(style)
    }
}
