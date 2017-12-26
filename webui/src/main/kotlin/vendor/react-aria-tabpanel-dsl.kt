package vendor

import react.RBuilder
import react.RHandler

fun RBuilder.tab(id: TabId, contents: RHandler<TabProps>) = child(Tab::class) {
    attrs.id = id
    contents()
}

fun RBuilder.tabList(contents: RHandler<TabListProps>) = child(TabList::class, contents)

fun RBuilder.tabPanel(tabId: TabId, contents: RHandler<TabPanelProps>) = child(TabPanel::class) {
    attrs.tabId = tabId
    contents()
}

fun RBuilder.tabPanelWrapper(contents: RHandler<TabPanelWrapperProps>) = child(TabPanelWrapper::class, contents)
