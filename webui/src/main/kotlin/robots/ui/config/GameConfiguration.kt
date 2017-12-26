package robots.ui.config

import react.RBuilder
import react.dom.div
import robots.ui.ActionCardSuit
import robots.ui.BrowserSpeech
import vendor.tab
import vendor.tabList
import vendor.tabPanelWrapper

fun RBuilder.gameConfiguration(actions: ActionCardSuit, speech: BrowserSpeech, updateActions: (ActionCardSuit) -> Unit) {
    tabPanelWrapper {
        attrs.className = "dialog-tabs"
        
        tabList {
            attrs.tag = "ul"
            tab("actions-tab") {
                attrs.tag = "li"
                +"Actions"
            }
            tab("speech-tab") {
                attrs.tag = "li"
                +"Speech"
            }
        }
        
        div("tab-panels") {
            actionsConfiguration(actions, speech, updateActions)
            speechConfiguration(speech)
        }
    }
}

