package robots.ui

import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.button
import react.dom.div
import robots.Reduction
import robots.UndoRedoStack
import robots.canUndo


fun RBuilder.runHeaderControls(game: Running, speech: Speech, update: (GameState) -> Unit) {
    controlGroup {
        button(classes="backwards") {
            attrs.title = "Single-step backwards"
            attrs.disabled = speech.isSpeaking || !game.canUndoStep()
            attrs.onClickFunction = { update(game.undoStep()) }
            +"<<"
        }
        button(classes="forwards") {
            attrs.title = "Single-step forwards"
            attrs.disabled = speech.isSpeaking || !game.canRedoStep()
            attrs.onClickFunction = { update(game.redoStep()) }
            +">>"
        }
    }
    
    controlGroup {
        button {
            attrs.title = "Stop running and return to edit mode"
            attrs.disabled = speech.isSpeaking
            attrs.onClickFunction = { update(game.stopRunning()) }
            +"Stop"
        }
    }
}

fun RBuilder.runControlPanel(game: Running, speech: Speech, update: (GameState) -> Unit) {
    val trace = game.trace
    
    div("controls") {
        if (!trace.canUndo()) {
            startControls(game, speech, update)
        }
        else {
            stepControls(trace, game, speech, update)
        }
    }
}

private fun RDOMBuilder<DIV>.stepControls(trace: UndoRedoStack<Reduction>, game: Running, speech: Speech, update: (GameState) -> Unit) {
    div("action-text") {
        trace.current.action?.let { +("\"" + it.text + "\"") }
    }
    
    buttonBar {
        if (game.hasFinished()) {
            richButton("Stop running and return to edit mode", "Finished", "\uD83D\uDC4D", speech.isSpeaking) {
                update(game.stopRunning())
            }
        }
        else {
            richButton("Run next action", "Next", "\uD83D\uDC4D", speech.isSpeaking) {
                performStep(game, speech, update)
            }
        }
        
        richButton("Action failed", "Failed", "\uD83D\uDC4E", speech.isSpeaking || true) {
            console.log("failure not implemented yet")
        }
    }
}

private fun RDOMBuilder<DIV>.startControls(game: Running, speech: Speech, update: (GameState) -> Unit) {
    div("action-text") {
        +"Are you ready?"
    }
    buttonBar {
        button {
            attrs.onClickFunction = { performStep(game, speech, update) }
            attrs.disabled = speech.isSpeaking || game.hasFinished()
            attrs.title = "Start the program"
            
            div("button-icon") { +"▶︎" }
            div("button-text") { +"Start" }
        }
    }
}

private fun performStep(game: Running, speech: Speech, update: (GameState) -> Unit) {
    if (game.hasFinished()) {
        update(game.stopRunning())
    }
    else {
        val next = game.step()
        update(next)
        speech.speak(next.trace.current.action?.text, onSpoken = { console.log("done") })
    }
}
