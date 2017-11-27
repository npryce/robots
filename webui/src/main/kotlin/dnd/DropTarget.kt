package dnd

import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div


class DropTarget(props: DropTarget.Props): RComponent<DropTarget.Props, RState>(props) {
    interface Props: RProps {
        var canAccept: (Any)->Boolean
        var accept: (Any)->Unit
    }
    
    private fun dragIn(ev: Event) {
        val detail: DragInDetail = ev.detail() ?: return
        detail.acceptable = props.canAccept(detail.data)
    }
    
    private fun dragOut(it: Event) {
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
                if (elt != null) {
                    elt.addEventListener(DND_DRAG_IN, ::dragIn)
                    elt.addEventListener(DND_DRAG_OUT, ::dragOut)
                    elt.addEventListener(DND_DROP, ::drop)
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

