package dnd

import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RState
import react.dom.div

external interface DropTargetProps : RProps {
    var canAccept: (Any) -> Boolean
    var accept: (Any) -> Unit
}

external interface DropTargetState : RState {
    var isDraggedOver: Boolean
}

class DropTarget(props: DropTargetProps) : RComponent<DropTargetProps, DropTargetState>(props) {
    init {
        state.isDraggedOver = false
    }
    
    private var targetElement: Element? = null
    
    override fun RBuilder.render() {
        div("drop-target") {
            ref { elt: Element? -> targetElement = elt }
            children()
        }
    }
    
    override fun componentDidMount() {
        targetElement?.apply {
            addEventListener(DND_DRAG_IN, ::dragIn)
            addEventListener(DND_DRAG_OUT, ::dragOut)
            addEventListener(DND_DROP, ::drop)
        }
    }
    
    override fun componentWillUnmount() {
        targetElement?.apply {
            removeEventListener(DND_DRAG_IN, ::dragIn)
            removeEventListener(DND_DRAG_OUT, ::dragOut)
            removeEventListener(DND_DROP, ::drop)
        }
    }
    
    private fun dragIn(ev: Event) {
        val detail: DragInDetail = ev.detail() ?: return
        val canAccept = props.canAccept(detail.data)
        setState({ it.apply { isDraggedOver = canAccept } })
        detail.acceptable = canAccept
    }
    
    private fun dragOut(@Suppress("UNUSED_PARAMETER") ev: Event) {
        setState({ it.apply { isDraggedOver = false } })
    }
    
    private fun drop(ev: Event) {
        val detail: DropDetail = ev.detail() ?: return
        
        if (props.canAccept(detail.data)) {
            props.accept(detail.data)
            detail.accepted = true
        }
        else {
            detail.accepted = false
        }
    }
}

fun RBuilder.dropTarget(canAccept: (Any) -> Boolean, accept: (Any) -> Unit, children: RElementBuilder<RProps>.() -> Unit) =
    child(DropTarget::class) {
        attrs.canAccept = canAccept
        attrs.accept = accept
        children()
    }

