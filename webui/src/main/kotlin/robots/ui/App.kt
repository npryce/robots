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
        header(state.undo, update = ::updateUndoRedoStack)
        programEditor(props.cards, state.undo.current, onEdit = ::pushUndoRedoState)
        controlPanel(props.cards)
    }
    
    private fun pushUndoRedoState(newProgram: Seq) {
        updateUndoRedoStack(state.undo.havingDone(newProgram))
    }
    
    private fun updateUndoRedoStack(newState: UndoRedoStack<Seq>) {
        setState({ it.apply { undo = newState } })
    }
}

fun RBuilder.app(cards: Deck, initialProgram: Seq = Seq()) = child(App::class) {
    attrs.program = initialProgram
    attrs.cards = cards
}

inline fun RBuilder.controlGroup(contents: RBuilder.() -> Unit) {
    span("control-group", contents)
}

fun RBuilder.header(undoStack: UndoRedoStack<Seq>, update: (UndoRedoStack<Seq>) -> Unit) {
    div("header") {
        span("score") {
            +"Cost: "
            span("cost") { +"¢${undoStack.current.cost()}" }
        }
        
        undoRedoButtons(undoStack, update)
        
        controlGroup {
            button(classes = "run") {
                attrs.onClickFunction = { ev: dynamic -> // is this the only way to access event properties?
                    if (ev.altKey) {
                        console.log("slo-mo")
                        run(Seq::reduce, undoStack, update)
                    }
                    else {
                        run(Seq::reduceToAction, undoStack, update)
                    }
                    ev.preventDefault()
                }
                +"Run"
            }
        }
    }
}


fun run(performStep: Seq.() -> Reduction, undoStack: UndoRedoStack<Seq>, update: (UndoRedoStack<Seq>) -> Unit) {
    val (action, future) = undoStack.current.performStep()
    
    fun done() {
        if (future != null) {
            update(undoStack.havingDone(future))
        }
    }
    
    if (action != null) {
        speechSynthesis.speak(SpeechSynthesisUtterance(action.text).apply {
            onend = { done() }
        })
    }
    else {
        done()
    }
}


private fun RBuilder.undoRedoButtons(undoStack: UndoRedoStack<Seq>, update: (UndoRedoStack<Seq>) -> Unit) {
    controlGroup {
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
}

private fun RBuilder.controlPanel(style: Deck) {
    div("controls") {
        cardStacks(style)
    }
}
