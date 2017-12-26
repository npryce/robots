@file:JsModule("react-aria-tabpanel")
package vendor

import react.RProps
import react.RState
import react.React


external interface TabProps : RProps {
    var id: TabId // required
    var tag: String
    var index: Int
    var active: Boolean
    var letterNavigationText: String
    
    @JsName("class")
    var className: String
}

external class Tab : React.Component<TabProps, RState> {
    override fun render()
}

external interface TabListProps : RProps {
    var tag: String
    
    @JsName("class")
    var className: String
}

external class TabList : React.Component<TabListProps, RState> {
    override fun render()
}

external interface TabPanelProps : RProps {
    var tabId: TabId // required
    var tag: String
    var active: Boolean
    
    @JsName("class")
    var className: String
    
}

external class TabPanel : React.Component<TabPanelProps, RState> {
    override fun render()
}

external interface TabPanelWrapperProps: RProps {
    var activeTabId: TabId
    var letterNavigation: Boolean
    var onChange: (selectedTab: TabId)->Unit
    var tag: String
    
    @JsName("class")
    var className: String
    
}

@JsName("Wrapper")
external class TabPanelWrapper : React.Component<TabPanelWrapperProps, RState> {
    override fun render()
}
