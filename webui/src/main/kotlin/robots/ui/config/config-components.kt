package robots.ui.config

import kotlinx.html.DIV
import react.RBuilder
import react.RHandler
import react.dom.RDOMBuilder
import react.dom.div
import vendor.TabPanelProps
import vendor.tabPanel


fun RBuilder.configPanel(configuredThings: String, contents: RHandler<TabPanelProps>) {
    tabPanel(configuredThings+"-tab") {
        attrs.className = "config-panel config-$configuredThings-panel"
        contents()
    }
}

fun RBuilder.buttonBar(contents: RDOMBuilder<DIV>.() -> Unit) {
    div("button-bar", contents)
}

val configItemsClass = "config-items"
