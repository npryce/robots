package robots.ui

import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.title
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.a
import react.dom.div
import react.dom.header
import react.dom.span
import react.setState
import robots.Seq
import robots.UndoRedoStack
import robots.cost
import robots.havingDone
import robots.ui.config.gameConfiguration
import vendor.ariaModal


external interface AppProps : RProps {
    var program: Seq
    var initialCards: Deck
}

external interface AppState : RState {
    var game: GameState
    var configurationShowing: Boolean
    var cards: Deck
}

class App(props: AppProps) : RComponent<AppProps, AppState>(props) {
    val speech: BrowserSpeech = BrowserSpeech { speechChanged() }
    
    init {
        state.game = initialGameState()
        state.configurationShowing = false
        state.cards = props.initialCards
    }
    
    override fun RBuilder.render() {
        console.log("(re)rendering")
        val game = state.game
        
        header()
        
        if (state.configurationShowing) {
            configurationDialog()
        }
        
        when (game) {
            is Running -> {
                programEditor(state.cards, game.currentState, onEdit = {})
                runControlPanel(game, speech, ::updateGameState)
            }
            is Editing -> {
                programEditor(state.cards, currentProgram, onEdit = ::pushUndoRedoState)
                editControlPanel()
            }
        }
    }
    
    private fun RBuilder.editControlPanel() {
        div("controls") {
            cardStacks(state.cards, onEdit = ::pushUndoRedoState)
        }
    }
    
    fun RBuilder.header() {
        val game = state.game
        
        header {
            controlGroup { configureButton() }
    
            span("score") {
                +"Cost: "
                span("cost") { +"¢${game.source.current.cost()}" }
            }
            
            when (game) {
                is Editing -> editHeaderControls(game, speech, ::updateGameState)
                is Running -> runHeaderControls(game, speech, ::updateGameState)
            }
        }
    }
    
    private fun RBuilder.configureButton() {
        a {
            attrs.role = "button"
            attrs.title = "Configure the game"
            attrs.onClickFunction = { showConfigurationDialog(true) }
            +"⚙"
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
        updateUndoRedoStack(state.game.source.havingDone(newProgram))
    }
    
    private fun updateUndoRedoStack(newEditStack: UndoRedoStack<Seq>) {
        updateGameState(Editing(newEditStack))
    }
    
    private fun updateGameState(newState: GameState) {
        console.log("updating game state")
        setState { game = newState }
    }
    
    private fun speechChanged() {
        forceUpdate()
    }
    
    private val currentProgram get() = game.source.current
    private val game get() = state.game
}


fun RBuilder.app(cards: Deck, initialProgram: Seq = Seq()) = child(App::class) {
    attrs.program = initialProgram
    attrs.initialCards = cards
}

inline fun RBuilder.controlGroup(contents: RBuilder.() -> Unit) {
    span("control-group", contents)
}


