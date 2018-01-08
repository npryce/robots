package robots.ui

import kotlinx.html.DIV
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.div

fun RBuilder.buttonBar(contents: RDOMBuilder<DIV>.() -> Unit) {
    div("button-bar", contents)
}
