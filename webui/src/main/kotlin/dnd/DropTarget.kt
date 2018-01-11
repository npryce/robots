package dnd

import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RState
import react.dom.div
import react.dom.set

external interface DropTargetProps : RProps {
    var canAccept: (Any) -> Boolean
    var accept: (Any) -> Unit
    var classes: String
    var disabled: Boolean
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
        div(props.classes) {
            attrs["aria-disabled"] = props.disabled.toString()
            ref { elt: Element? -> targetElement = elt }
            children()
        }
    }
    
    override fun componentDidMount() {
        if (!props.disabled) {
            targetElement?.apply {
                addEventListener(DND_DRAG_IN, ::dragIn)
                addEventListener(DND_DRAG_OUT, ::dragOut)
                addEventListener(DND_DROP, ::drop)
            }
        }
    }
    
    override fun componentWillUnmount() {
        if (!props.disabled) {
            targetElement?.apply {
                removeEventListener(DND_DRAG_IN, ::dragIn)
                removeEventListener(DND_DRAG_OUT, ::dragOut)
                removeEventListener(DND_DROP, ::drop)
            }
        }
    }
    
    private fun dragIn(ev: Event) {
        if (!ev.defaultPrevented) {
            val detail: DragInDetail = ev.detail() ?: return
            val canAccept = props.canAccept(detail.data)
            setState({ it.apply { isDraggedOver = canAccept } })
            detail.acceptable = canAccept
            
            ev.preventDefault()
        }
    }
    
    private fun dragOut(@Suppress("UNUSED_PARAMETER") ev: Event) {
        if (!ev.defaultPrevented) {
            setState({ it.apply { isDraggedOver = false } })
            
            ev.preventDefault()
        }
    }
    
    private fun drop(ev: Event) {
        if (!ev.defaultPrevented) {
            val detail: DropDetail = ev.detail() ?: return
            
            if (props.canAccept(detail.data)) {
                props.accept(detail.data)
                detail.accepted = true
            }
            else {
                detail.accepted = false
            }
            
            ev.preventDefault()
        }
    }
}

fun RBuilder.dropTarget(
    canAccept: (Any) -> Boolean,
    accept: (Any) -> Unit,
    classes: String = "drop-target",
    disabled: Boolean = false,
    children: RElementBuilder<DropTargetProps>.() -> Unit = {}
) =
    child(DropTarget::class) {
        attrs.canAccept = canAccept
        attrs.accept = accept
        attrs.classes = classes
        attrs.disabled = disabled
        children()
    }

