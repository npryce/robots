package dnd

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.RBuilder
import react.dom.div


fun RBuilder.dropTarget(
    canAccept: (Any)->Boolean,
    accept: (Any)->Unit,
    contents: RBuilder.()->Unit)
{
    fun dragIn(ev: Event) {
        val detail: DragInDetail = ev.detail() ?: return
        detail.acceptable = canAccept(detail.data)
    }
    
    fun dragOut(it: Event) {
    }
    
    fun drop(ev: Event) {
        val detail: DropDetail = ev.detail() ?: return
        
        if (canAccept(detail.data)) {
            accept(detail.data)
            detail.accepted = true
        } else {
            detail.accepted = false
        }
    }
    
    div("drop-target") {
        ref { elt: EventTarget ->
            elt.addEventListener(DND_DRAG_IN, ::dragIn)
            elt.addEventListener(DND_DRAG_OUT, ::dragOut)
            elt.addEventListener(DND_DROP, ::drop)
        }
        
        contents()
    }
}

