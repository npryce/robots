package dnd

import kotlinx.html.classes
import org.w3c.dom.CustomEvent
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.set
import react.setState
import kotlin.browser.window

external interface DraggableProps : RProps {
    var dataProvider: () -> Any
    var classes: String
}

external interface DraggableState : RState {
    var isBeingDragged: Boolean
}

class Draggable(props: DraggableProps) : RComponent<DraggableProps, DraggableState>(props) {
    private var draggableElement: Element? = null
    
    init {
        state.isBeingDragged = false
    }
    
    override fun RBuilder.render() {
        div(props.classes) {
            if (state.isBeingDragged) attrs.classes += "dragged"
            attrs["aria-disabled"] = "false"
            
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
        if (!ev.defaultPrevented) {
            ev as CustomEvent
            val sourceElement = ev.currentTarget as HTMLElement
            val detail = ev.detail as DragStartDetail
            
            detail.element = sourceElement.deepClone()
            detail.elementOrigin = sourceElement.pageOrigin()
            detail.data = props.dataProvider()
            detail.notifyDragStop = { stopDrag() }
            
            setState { isBeingDragged = true }
            
            ev.preventDefault()
        }
    }
    
    private fun stopDrag() {
        setState { isBeingDragged = false }
    }
    
    private fun Element.pageOrigin(): Point {
        val box = getBoundingClientRect()
        return Point(box.left + window.pageXOffset, box.top + window.pageYOffset)
    }
}

fun RBuilder.draggable(
    dataProvider: () -> Any,
    classes: String = "draggable",
    disabled: Boolean = false,
    contents: RBuilder.() -> Unit
) =
    if (disabled)
        div(classes) {
            attrs["aria-disabled"] = "true"
            contents()
        }
    else
        child(Draggable::class) {
            attrs.dataProvider = dataProvider
            attrs.classes = classes
            contents()
        }
