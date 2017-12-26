package robots.ui.config

import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import org.w3c.dom.events.InputEvent
import react.RBuilder
import react.dom.button
import react.dom.input
import react.dom.table
import react.dom.tbody
import react.dom.td
import react.dom.tr
import robots.Action
import robots.ui.ActionCardStyle
import robots.ui.ActionCardSuit
import robots.ui.Speech
import robots.ui.handler
import robots.ui.newValue


fun RBuilder.actionsConfiguration(actions: ActionCardSuit, speech: Speech, updateActions: (ActionCardSuit) -> Unit) {
    configPanel("actions") {
        table(configItemsClass) {
            tbody {
                actions.forEachIndexed { i, card ->
                    tr {
                        actionCardEditor(
                            card = card,
                            speech = speech,
                            update = { updateActions(actions.replace(i, it)) },
                            delete = { updateActions(actions.remove(it)) }
                        )
                    }
                }
            }
        }
        buttonBar {
            button {
                attrs.title = "Add new action"
                attrs.onClickFunction = { ev ->
                    updateActions(actions.add(ActionCardStyle("?", Action("Change this"))))
                    ev.stopPropagation()
                }
                +"+"
            }
        }
    }
}

private fun RBuilder.actionCardEditor(
    card: ActionCardStyle,
    speech: Speech,
    update: (ActionCardStyle) -> Unit,
    delete: (ActionCardStyle) -> Unit
) {
    td {
        input {
            attrs.width = "2"
            attrs.onChangeFunction = handler<InputEvent> {
                update(card.copy(face = it.newValue))
            }
            attrs.value = card.face
        }
    }
    td {
        input {
            attrs.value = card.explanation
            attrs.disabled = speech.isSpeaking
            attrs.onChangeFunction = handler<InputEvent> {
                update(card.copy(value = Action(it.newValue)))
            }
        }
    }
    td {
        button {
            attrs.disabled = speech.isSpeaking
            attrs.onClickFunction = { speech.speak(card.explanation) }
            +"▶︎"
        }
    }
    td {
        button {
            attrs.disabled = speech.isSpeaking
            attrs.onClickFunction = { delete(card) }
            +"-"
        }
    }
}
