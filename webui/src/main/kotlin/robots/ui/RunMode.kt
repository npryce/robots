package robots.ui

import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.title
import react.RBuilder
import react.dom.a
import react.dom.button
import react.dom.div


fun RBuilder.runHeaderControls(game: Running, speech: Speech, update: (GameState) -> Unit) {
    controlGroup {
        a {
            attrs.role = "button"
            attrs.title = "Single-step backwards"
            +"<<"
        }
        a {
            attrs.role = "button"
            attrs.title = "Single-step forwards"
            +">>"
        }
    }
    
    controlGroup {
        button {
            +"Stop"
        }
    }
}

fun RBuilder.runControlPanel(game: Running, speech: Speech, update: (GameState) -> Unit) {
    div("controls") {
        div("action-text") {
            if (game.trace != null) {
                game.trace.current.action?.let { +("\"" + it.text + "\"") }
            }
        }
        
        buttonBar {
            if (game.isAtStart()) {
                button {
                    attrs.onClickFunction = { performStep(game, speech, update) }
                    attrs.disabled = speech.isSpeaking || game.hasFinished()
                    attrs.title = "Start running"
                    +"Start"
                }
            }
            else {
                button {
                    attrs.onClickFunction = { performStep(game, speech, update) }
                    attrs.disabled = speech.isSpeaking || game.hasFinished()
                    attrs.title = "Run next action"
                    +"Next"
                }
                button {
                    attrs.disabled = speech.isSpeaking || true
                    attrs.title = "Action failed"
                    +"Failed"
                }
            }
        }
    }
}

private fun performStep(game: Running, speech: Speech, update: (GameState) -> Unit) {
    val next = game.step()
    console.log("next = ", next)
    update(next)
    speech.speak(next.trace?.current?.action?.text, onSpoken = { console.log("done") })
}
