package robots.ui

import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.title
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.a
import react.dom.button
import react.dom.div
import react.dom.header
import react.dom.span
import react.setState
import robots.Reduction
import robots.Seq
import robots.UndoRedoStack
import robots.canRedo
import robots.canUndo
import robots.cost
import robots.havingDone
import robots.isNotRunnable
import robots.nop
import robots.redo
import robots.reduce
import robots.reduceToAction
import robots.ui.config.gameConfiguration
import robots.undo
import vendor.ariaModal


external interface AppProps : RProps {
    var program: Seq
    var initialCards: Deck
}

external interface AppState : RState {
    var undo: UndoRedoStack<Seq>
    var configurationShowing: Boolean
    var cards: Deck
}

class App(props: AppProps) : RComponent<AppProps, AppState>(props) {
    val speech: BrowserSpeech = BrowserSpeech { speechChanged() }
    
    init {
        state.undo = UndoRedoStack(props.program)
        state.configurationShowing = false
        state.cards = props.initialCards
    }
    
    override fun RBuilder.render() {
        header()
        if (state.configurationShowing) {
            configurationDialog()
        }
        programEditor(state.cards, currentProgram, onEdit = ::pushUndoRedoState)
        div("controls") {
            cardStacks(state.cards, onEdit = ::pushUndoRedoState)
        }
    }
    
    fun RBuilder.header() {
        header {
            controlGroup {
                a {
                    attrs.role = "button"
                    attrs.title = "Configure the game"
                    attrs.onClickFunction = { showConfigurationDialog(true) }
                    +"⚙"
                }
            }
            
            span("score") {
                +"Cost: "
                span("cost") { +"¢${currentProgram.cost()}" }
            }
            
            controlGroup {
                undoRedoButtons(
                    undoStack = state.undo,
                    disabled = speech.isSpeaking,
                    update = { updateUndoRedoStack(it) })
            }
            
            controlGroup {
                button(classes = "run") {
                    attrs.disabled = currentProgram.isNotRunnable() || speech.isSpeaking
                    
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
    
    private fun showConfigurationDialog(isShowing: Boolean) {
        setState { configurationShowing = isShowing }
    }
    
    private fun RBuilder.configurationDialog() {
        ariaModal {
            attrs.underlayClass = "dialog-underlay"
            attrs.includeDefaultStyles = false
            attrs.titleText = "Configure the game"
            attrs.onExit = { showConfigurationDialog(false) }
            gameConfiguration()
        }
    }
    
    private fun RBuilder.gameConfiguration() {
        gameConfiguration(state.cards.actionCards, speech) { newActionCards ->
            setState { cards = cards.copy(actionCards = newActionCards) }
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
            speech.speak(action.text) { updateUndoRedoStack(state.undo.undo().havingDone(next ?: nop)) }
        }
        else {
            updateUndoRedoStack(state.undo.havingDone(next ?: nop))
        }
    }
    
    private fun speechChanged() {
        console.log("speech changed")
        forceUpdate()
    }
    
    private val currentProgram get() = state.undo.current
}

fun RBuilder.app(cards: Deck, initialProgram: Seq = Seq()) = child(App::class) {
    attrs.program = initialProgram
    attrs.initialCards = cards
}

inline fun RBuilder.controlGroup(contents: RBuilder.() -> Unit) {
    span("control-group", contents)
}


private fun RBuilder.undoRedoButtons(undoStack: UndoRedoStack<Seq>, disabled: Boolean = false, update: (UndoRedoStack<Seq>) -> Unit) {
    button(classes = "undo") {
        attrs.onClickFunction = { update(undoStack.undo()) }
        attrs.disabled = disabled || !undoStack.canUndo()
        +"Undo"
    }
    button(classes = "redo") {
        attrs.onClickFunction = { update(undoStack.redo()) }
        attrs.disabled = disabled || !undoStack.canRedo()
        +"Redo"
    }
}

