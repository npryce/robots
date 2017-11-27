package dnd

import react.RBuilder
import react.dom.div


fun RBuilder.draggable(dataProvider: ()->Any, children: RBuilder.()->Unit) {
    div("draggable") {
        ref { elt -> DragAndDrop.makeDraggable(elt, dataProvider) }
        children()
    }
}
