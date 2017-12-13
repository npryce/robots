package dnd

import browser.Touch
import browser.TouchEvent
import browser.TouchId
import browser.TouchList
import browser.get
import org.w3c.dom.CustomEvent
import org.w3c.dom.CustomEventInit
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.css.ElementCSSInlineStyle
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

class DragStartDetail(
    var data: Any? = null,
    var element: HTMLElement? = null,
    var elementOrigin: Point? = null,
    var notifyDragStop: ()->Unit = {}
)

class DragInDetail(
    val data: Any,
    var acceptable: Boolean = false
)

class DropDetail(
    val data: Any,
    var accepted: Boolean = false
)

internal const val DND_DRAG_START = "dnd-drag-start"
internal const val DND_DRAG_IN = "dnd-drag-in"
internal const val DND_DRAG_OUT = "dnd-drag-out"
internal const val DND_DROP = "dnd-drop"

private fun eventInit(detail: Any?) = CustomEventInit(bubbles = true, cancelable = true, detail = detail)

@Suppress("FunctionName")
fun DragStartEvent(detail: DragStartDetail) = CustomEvent(DND_DRAG_START, eventInit(detail))

@Suppress("FunctionName")
fun DragInEvent(detail: DragInDetail) = CustomEvent(DND_DRAG_IN, eventInit(detail))

@Suppress("FunctionName")
fun DragOutEvent() = Event(DND_DRAG_OUT, eventInit(true))

@Suppress("FunctionName")
fun DropEvent(detail: DropDetail) = CustomEvent(DND_DROP, eventInit(detail))


private fun ElementCSSInlineStyle.setPosition(p: Point) {
    style.left = "${p.x}px"
    style.top = "${p.y}px"
}

private val animationEndEvents = listOf("animationend", "animationcancel", "transitionend", "transitioncancel")

private class DragState(
    val data: Any,
    val sourceElement: Element,
    val draggedElement: HTMLElement,
    val draggedElementOrigin: Point,
    val gestureOrigin: Point,
    val touchId: TouchId?,
    val notifyDragStop: () -> Unit,
    
    var dropTarget: Element? = null
)

internal inline fun <reified T : Node> T.deepClone() = cloneNode(true) as T

object DragAndDrop {
    private var dragState: DragState? = null
    
    private fun startDragging(target: Element, pageX: Double, pageY: Double, touchId: TouchId? = null) =
        startDragging(target, Point(pageX, pageY), touchId)
    
    private fun startDragging(target: Element, startPosition: Point, touchId: TouchId? = null): Boolean {
        dragState?.draggedElement?.remove()
        
        val dragDetail = DragStartDetail()
        target.dispatchEvent(DragStartEvent(dragDetail))
        val draggedData = dragDetail.data ?: return false
        val draggedElement = dragDetail.element ?: return false
        val sourcePos = dragDetail.elementOrigin ?: return false
        
        draggedElement.apply {
            classList.add("dragging")
            setPosition(sourcePos)
        }
        
        dragState = DragState(
            data = draggedData,
            sourceElement = target,
            draggedElement = draggedElement,
            draggedElementOrigin = sourcePos,
            gestureOrigin = startPosition,
            touchId = touchId,
            notifyDragStop = dragDetail.notifyDragStop)
        
        document.body?.appendChild(draggedElement)
        
        return true
    }
    
    private fun dragTo(pageX: Double, pageY: Double) {
        dragTo(Point(pageX, pageY))
    }
    
    private fun dragTo(pagePos: Point) {
        val dragState = this.dragState ?: return // if drag_state is null, drag must have been cancelled
        val dropTarget = dragState.dropTarget
        
        dragState.draggedElement.setPosition(dragState.draggedElementOrigin + (pagePos - dragState.gestureOrigin))
        
        val under = document.elementFromPoint(pagePos.x, pagePos.y)
        
        if (under == dropTarget) return
        
        if (dropTarget != null) {
            dropTarget.dispatchEvent(DragOutEvent())
            dragState.dropTarget = null
        }
        
        if (under != null) {
            val dragInDetail = DragInDetail(dragState.data)
            under.dispatchEvent(DragInEvent(dragInDetail))
            if (dragInDetail.acceptable) {
                dragState.dropTarget = under
            }
        }
    }
    
    private fun drop() {
        val dragState = this.dragState ?: return
        val dropTarget = dragState.dropTarget
        val draggedElement = dragState.draggedElement
        
        val dropIsAccepted =
            if (dropTarget != null) {
                val dropDetail = DropDetail(dragState.data)
                dropTarget.dispatchEvent(DropEvent(dropDetail))
                dropDetail.accepted
            }
            else {
                false
            }
        
        animationEndEvents.forEach { eventName ->
            draggedElement.addEventListener(eventName, {draggedElement.remove()})
        }
        
        if (dropIsAccepted) {
            draggedElement.remove()
        }
        else {
            draggedElement.classList.add("rejected")
        }
        
        dragState.notifyDragStop()
        
        this.dragState = null
    }
    
    private fun bodyMouseDown(ev: Event) {
        ev as MouseEvent
        
        if (ev.button.toInt() == 0 && startDragging(ev.target as HTMLElement, ev.pageX, ev.pageY)) {
            ev.preventDefault()
            document.body?.addEventListener("mousemove", ::bodyMouseDrag, true)
        }
    }
    
    private fun bodyMouseDrag(ev: Event) {
        ev as MouseEvent
        if (dragState != null) {
            ev.preventDefault()
            dragTo(ev.pageX, ev.pageY)
        }
    }
    
    private fun bodyMouseUp(ev: Event) {
        ev as MouseEvent
        if (dragState != null) {
            ev.preventDefault()
            document.body?.removeEventListener("mousemove", ::bodyMouseDrag, true)
            drop()
        }
    }
    
    private fun TouchList.touchWithId(id: TouchId): Touch? {
        return (0 until length)
            .map { i -> this[i] }
            .find { it.identifier == id }
    }
    
    private fun TouchList.containsTouchWithId(id: TouchId): Boolean {
        return touchWithId(id) != null
    }
    
    private fun bodyTouchStart(ev: Event) {
        ev as TouchEvent
        
        val touch = ev.changedTouches[0]
    
        if (startDragging(touch.target, touch.pageX, touch.pageY, touch.identifier)) {
            ev.preventDefault()
            document.body?.addEventListener("touchmove", ::bodyTouchDrag, true)
        }
    }
    
    private fun bodyTouchDrag(ev: Event) {
        ev as TouchEvent
        val dragState = this.dragState ?: return
        val touchId = dragState.touchId ?: return
        val touch = ev.changedTouches.touchWithId(touchId) ?: return
        
        ev.preventDefault()
        dragTo(touch.pageX, touch.pageY)
    }
    
    private fun bodyTouchEnd(ev: Event) {
        ev as TouchEvent
        val dragState = this.dragState ?: return
        val touchId = dragState.touchId ?: return
        
        if (ev.changedTouches.containsTouchWithId(touchId)) {
            ev.preventDefault()
            document.body?.removeEventListener("touchmove", ::bodyTouchDrag, true)
            drop()
        }
    }
    
    fun activate() {
        val body = document.body ?: return
        body.addEventListener("mousedown", ::bodyMouseDown)
        body.addEventListener("mouseup", ::bodyMouseUp)
        
        body.addEventListener("touchstart", ::bodyTouchStart)
        body.addEventListener("touchend", ::bodyTouchEnd)
        body.addEventListener("touchcancel", ::bodyTouchEnd)
    }
}


internal inline fun <reified T> Event.detail(): T? =
    (this as? CustomEvent)?.detail as? T