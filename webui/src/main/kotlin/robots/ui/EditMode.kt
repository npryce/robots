package robots.ui

import kotlinx.html.HEADER
import kotlinx.html.js.onClickFunction
import react.dom.RDOMBuilder
import react.dom.button
import robots.isNotRunnable

fun RDOMBuilder<HEADER>.editHeaderControls(game: Editing, speech: Speech, update: (GameState) -> Unit) {
    val program = game.source.current
    
    controlGroup {
        undoRedoButtons(
            undoStack = game.source,
            disabled = speech.isSpeaking || game.isRunning(),
            update = { update(Editing(it)) })
    }
    
    controlGroup {
        button(classes = "run") {
            attrs.disabled = program.isNotRunnable() || speech.isSpeaking
            attrs.onClickFunction = { ev ->
                update(game.startRunning())
                ev.preventDefault()
            }
            
            +"Run"
        }
    }
}

