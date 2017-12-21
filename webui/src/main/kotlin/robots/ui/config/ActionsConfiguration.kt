package robots.ui.config

import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.InputEvent
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button
import react.dom.div
import react.dom.input
import react.dom.table
import react.dom.tbody
import react.dom.td
import react.dom.th
import react.dom.thead
import react.dom.tr
import robots.Action
import robots.ui.ActionCardStyle
import robots.ui.ActionCardSuit
import robots.ui.Speech
import robots.ui.handler
import robots.ui.newValue


fun RBuilder.actionCardEditor(
    card: ActionCardStyle,
    speech: Speech,
    update: (ActionCardStyle) -> Unit,
    delete: (ActionCardStyle) -> Unit
) {
    td {
        input {
            attrs.width = "2"
            attrs.maxLength = "1"
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

interface ActionsConfigurationProps : RProps {
    var actions: ActionCardSuit
    var speech: Speech
    var updateActions: (ActionCardSuit) -> Unit
}

interface ActionsConfigurationState : RState {
}

class ActionsConfiguration : RComponent<ActionsConfigurationProps, ActionsConfigurationState>() {
    override fun RBuilder.render() {
        table {
            thead {
                tr {
                    th { +"Emoji" }
                    th { +"Action" }
                    th { }
                    th { }
                }
            }
            tbody("action-editors") {
                props.actions.forEachIndexed { i, a ->
                    tr {
                        actionCardEditor(
                            card = a,
                            speech = props.speech,
                            update = { props.updateActions(props.actions.replace(i, it)) },
                            delete = { props.updateActions(props.actions.remove(it)) }
                        )
                    }
                }
            }
        }
        div {
            button {
                attrs.onClickFunction = { ev ->
                    props.updateActions(props.actions.add(ActionCardStyle("?", Action("Change this"))))
                    ev.stopPropagation()
                }
                +"+"
            }
        }
    }
    
    private fun preview(text: String) {
        props.speech.speak(text)
    }
}

fun RBuilder.actionsConfiguration(actions: ActionCardSuit, speech: Speech, updateActions: (ActionCardSuit) -> Unit) =
    child(ActionsConfiguration::class) {
        attrs.actions = actions
        attrs.updateActions = updateActions
        attrs.speech = speech
    }
