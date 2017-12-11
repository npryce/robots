package dnd

import kotlinx.html.classes
import org.w3c.dom.CustomEvent
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RState
import react.dom.div
import react.setState
import kotlin.browser.window

external interface DraggableProps : RProps {
    var dataProvider: () -> Any
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
        div("draggable") {
            if (state.isBeingDragged) attrs.classes += "dragged"
            
            ref { elt: Element? -> draggableElement = elt }
            children()
        }
    }
    
    override fun componentDidMount() {
        draggableElement?.addEventListener(DND_DRAG_START, ::startDrag)
        draggableElement?.addEventListener(DND_DRAG_STOP, ::stopDrag)
    }
    
    override fun componentWillUnmount() {
        draggableElement?.removeEventListener(DND_DRAG_START, ::startDrag)
        draggableElement?.removeEventListener(DND_DRAG_STOP, ::stopDrag)
    }
    
    private fun startDrag(ev: Event) {
        if (!ev.defaultPrevented) {
            console.log(ev)
            ev as CustomEvent
            val sourceElement = ev.currentTarget as HTMLElement
            val detail = ev.detail as DragStartDetail
    
            detail.element = sourceElement.deepClone()
            detail.elementOrigin = sourceElement.pageOrigin()
            detail.data = props.dataProvider()
    
            setState { isBeingDragged = true }
    
            ev.preventDefault()
        }
//        ev.stopPropagation()
    }
    
    private fun stopDrag(ev: Event) {
        setState { isBeingDragged = false }
    }
    
    private fun Element.pageOrigin(): Point {
        val box = getBoundingClientRect()
        return Point(box.left + window.pageXOffset, box.top + window.pageYOffset)
    }
}

fun RBuilder.draggable(dataProvider: () -> Any, contents: RElementBuilder<RProps>.() -> Unit) = child(Draggable::class) {
    attrs.dataProvider = dataProvider
    contents()
}
