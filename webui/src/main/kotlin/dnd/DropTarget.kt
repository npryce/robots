package dnd

import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div


class DropTarget(props: DropTarget.Props): RComponent<DropTarget.Props, DropTarget.State>(props) {
    interface Props: RProps {
        var canAccept: (Any)->Boolean
        var accept: (Any)->Unit
    }
    
    data class State(val isDraggedOver: Boolean): RState
    
    init {
        state = State(isDraggedOver = false)
    }
    
    private fun dragIn(ev: Event) {
        val detail: DragInDetail = ev.detail() ?: return
        val canAccept = props.canAccept(detail.data)
        setState({State(isDraggedOver = canAccept)})
        detail.acceptable = canAccept
    }
    
    private fun dragOut(@Suppress("UNUSED_PARAMETER") ev: Event) {
        setState({State(isDraggedOver = false)})
    }
    
    private fun drop(ev: Event) {
        val detail: DropDetail = ev.detail() ?: return
        
        if (props.canAccept(detail.data)) {
            props.accept(detail.data)
            detail.accepted = true
        } else {
            detail.accepted = false
        }
    }
    
    override fun RBuilder.render() {
        div("drop-target") {
            ref { elt: Element? ->
                elt?.apply {
                    addEventListener(DND_DRAG_IN, ::dragIn)
                    addEventListener(DND_DRAG_OUT, ::dragOut)
                    addEventListener(DND_DROP, ::drop)
                }
            }
            
            children()
        }
    }
}

fun RBuilder.dropTarget(canAccept: (Any)->Boolean, accept: (Any)->Unit, children: RBuilder.()->Unit) =
    child(DropTarget::class) {
        attrs.canAccept = canAccept
        attrs.accept = accept
        children()
    }

