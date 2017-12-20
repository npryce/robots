package robots.ui.config

import browser.SpeechSynthesisEvent
import browser.SpeechSynthesisUtterance
import browser.speechSynthesis
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
import react.setState
import robots.Action
import robots.ui.ActionCardPack
import robots.ui.ActionCardStyle
import robots.ui.handler
import robots.ui.newValue


fun RBuilder.actionCardEditor(
    card: ActionCardStyle,
    preview: (String) -> Unit,
    isPlaying: Boolean,
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
            attrs.onChangeFunction = handler<InputEvent> {
                update(card.copy(value = Action(it.newValue)))
            }
        }
    }
    td {
        button {
            attrs.disabled = isPlaying
            attrs.onClickFunction = { preview(card.explanation) }
            +"▶︎"
        }
    }
    td {
        button {
            attrs.disabled = isPlaying
            attrs.onClickFunction = { delete(card) }
            +"-"
        }
    }
}

interface ActionsConfigurationProps : RProps {
    var actions: ActionCardPack
    var updateActions: (ActionCardPack) -> Unit
}

interface ActionsConfigurationState : RState {
    var isPlaying: Boolean
}

class ActionsConfiguration : RComponent<ActionsConfigurationProps, ActionsConfigurationState>() {
    init {
        state.isPlaying = false
    }
    
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
                            preview = ::preview,
                            isPlaying = state.isPlaying,
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
        val end = handler<SpeechSynthesisEvent> { setState { isPlaying = false } }
        
        speechSynthesis.speak(SpeechSynthesisUtterance(text).apply {
            onend = end
            onerror = end
        })
        
        setState { isPlaying = true }
    }
}

fun RBuilder.actionsConfiguration(actions: ActionCardPack, updateActions: (ActionCardPack) -> Unit) =
    child(ActionsConfiguration::class) {
        attrs.actions = actions
        attrs.updateActions = updateActions
    }
