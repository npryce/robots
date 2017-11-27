package dnd

import browser.Touch
import browser.TouchEvent
import browser.TouchId
import browser.TouchList
import browser.get
import org.w3c.dom.CustomEvent
import org.w3c.dom.CustomEventInit
import org.w3c.dom.Element
import org.w3c.dom.EventInit
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.css.ElementCSSInlineStyle
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

class DragStartDetail(
    var data: Any? = null,
    var element: HTMLElement? = null,
    var elementOrigin: Point? = null
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

fun DragStartEvent(detail: DragStartDetail) = CustomEvent(DND_DRAG_START, CustomEventInit(bubbles = true, detail = detail))
fun DragInEvent(detail: DragInDetail) = CustomEvent(DND_DRAG_IN, CustomEventInit(bubbles = true, detail = detail))
fun DragOutEvent() = Event(DND_DRAG_OUT, EventInit(bubbles = true))
fun DropEvent(detail: DropDetail) = CustomEvent(DND_DROP, CustomEventInit(bubbles = true, detail = detail))

private fun ElementCSSInlineStyle.setPosition(p: Point) {
    style.left = "${p.x}px"
    style.top = "${p.y}px"
}

private class DragState(
    val data: Any,
    val draggedElement: HTMLElement,
    val draggedElementOrigin: Point,
    val gestureOrigin: Point,
    val touchId: TouchId?,
    
    var dropTarget: Element? = null
)

private inline fun <reified T : Node> T.deepClone() = cloneNode(true) as T

object DragAndDrop {
    private var dragState: DragState? = null
    
    private fun startDragging(target: Element, pageX: Double, pageY: Double, touchId: TouchId? = null) =
        startDragging(target, Point(pageX, pageY), touchId)
    
    private fun startDragging(target: Element, startPosition: Point, touchId: TouchId? = null): Boolean {
        dragState?.draggedElement?.remove()
        
        val dragDetail = DragStartDetail()
        target.dispatchEvent(DragStartEvent(dragDetail))
        val draggedData = dragDetail.data ?: return false
        val source_element = dragDetail.element ?: return false
        val sourcePos = dragDetail.elementOrigin ?: return false
        
        val draggedElement = source_element.deepClone().apply {
            classList.add("dragging")
            setPosition(sourcePos)
        }
        
        dragState = DragState(
            data = draggedData,
            draggedElement = draggedElement,
            draggedElementOrigin = sourcePos,
            gestureOrigin = startPosition,
            touchId = touchId)
        
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
        
        if (dropIsAccepted) {
            draggedElement.remove()
        }
        else {
            fun removeDraggedElement(ev: Event) {
                (ev.target as? Element)?.remove()
            }
            
            draggedElement.addEventListener("transitionend", ::removeDraggedElement)
            draggedElement.addEventListener("animationend", ::removeDraggedElement)
            
            draggedElement.classList.add("disappearing")
        }
        
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
        
        val touch = ev.changedTouches[0];
        
        if (startDragging(touch.target, touch.pageX, touch.pageY, touch.identifier)) {
            ev.preventDefault();
            document.body?.addEventListener("touchmove", ::bodyTouchDrag, true);
        }
    }
    
    private fun bodyTouchDrag(ev: Event) {
        ev as TouchEvent
        val dragState = this.dragState ?: return
        val touchId = dragState.touchId ?: return
        val touch = ev.changedTouches.touchWithId(touchId) ?: return
        
        ev.preventDefault();
        dragTo(touch.pageX, touch.pageY);
    }
    
    private fun bodyTouchEnd(ev: Event) {
        ev as TouchEvent
        val dragState = this.dragState ?: return
        val touchId = dragState.touchId ?: return
        
        if (ev.changedTouches.containsTouchWithId(touchId)) {
            ev.preventDefault();
            document.body?.removeEventListener("touchmove", ::bodyTouchDrag, true);
            drop();
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