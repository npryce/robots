package dnd

import org.w3c.dom.CustomEvent
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import kotlin.browser.window

interface DraggableProps : RProps {
    var dataProvider: () -> Any
}

class Draggable(props: DraggableProps) : RComponent<DraggableProps, RState>(props) {
    private var draggableElement: Element? = null
    
    override fun RBuilder.render() {
        div("draggable") {
            ref { elt: Element? -> draggableElement = elt }
            children()
        }
    }
    
    override fun componentDidMount() {
        draggableElement?.addEventListener(DND_DRAG_START, ::startDrag)
    }
    
    override fun componentWillUnmount() {
        draggableElement?.removeEventListener(DND_DRAG_START, ::startDrag)
    }
    
    private fun startDrag(ev: Event) {
        ev as CustomEvent
        val sourceElement = ev.currentTarget as HTMLElement
        val detail = ev.detail as DragStartDetail
        
        detail.element = sourceElement
        detail.elementOrigin = sourceElement.pageOrigin()
        detail.data = (props.dataProvider)()
    }
    
    private fun Element.pageOrigin(): Point {
        val box = getBoundingClientRect()
        return Point(box.left + window.pageXOffset, box.top + window.pageYOffset)
    }
}

fun RBuilder.draggable(dataProvider: () -> Any, contents: RBuilder.()->Unit) = child(Draggable::class) {
    attrs.dataProvider = dataProvider
    contents()
}
