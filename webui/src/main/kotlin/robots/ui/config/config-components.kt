package robots.ui.config

import react.RBuilder
import react.RHandler
import vendor.TabPanelProps
import vendor.tabPanel


fun RBuilder.configPanel(configuredThings: String, contents: RHandler<TabPanelProps>) {
    tabPanel(configuredThings+"-tab") {
        attrs.className = "config-panel config-$configuredThings-panel"
        contents()
    }
}

val configItemsClass = "config-items"
