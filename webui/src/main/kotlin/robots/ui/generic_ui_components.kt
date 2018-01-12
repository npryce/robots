package robots.ui

import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import org.w3c.dom.events.Event
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.button
import react.dom.div

/*
 * Common classews
 */

val backwards = "backwards"
val forwards = "forwards"


fun RBuilder.buttonBar(contents: RDOMBuilder<DIV>.() -> Unit) {
    div("button-bar", contents)
}

fun RDOMBuilder<DIV>.richButton(title: String, visibleText: String, icon: String, isDisabled: Boolean, onClick: (Event) -> Unit) {
    button {
        attrs.onClickFunction = onClick
        attrs.disabled = isDisabled
        attrs.title = title
        div("button-icon") { +icon }
        div("button-text") { +visibleText }
    }
}
