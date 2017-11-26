package robots.ui

import org.w3c.dom.CustomEvent
import org.w3c.dom.CustomEventInit
import org.w3c.dom.Element
import org.w3c.dom.EventInit
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.css.ElementCSSInlineStyle
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import robots.AST
import kotlin.browser.document
import kotlin.browser.window

class DragStartDetail(
    var data: AST? = null,
    var element: HTMLElement? = null,
    var elementOrigin: Point? = null
)

class DragInDetail(
    val data: AST,
    var acceptable: Boolean = false
)

class DropDetail(
    val data: AST,
    var accepted: Boolean = false
)

// Temporary, while converting code

fun DragStartEvent(detail: DragStartDetail) = CustomEvent("card-drag-start", CustomEventInit(bubbles = true, detail = detail))
fun DragInEvent(detail: DragInDetail) = CustomEvent("card-drag-in", CustomEventInit(bubbles = true, detail = detail))
fun DragOutEvent() = Event("card-drag-out", EventInit(bubbles = true))
fun DropEvent(detail: DropDetail) = CustomEvent("card-drop", CustomEventInit(bubbles = true, detail = detail))

data class Point(val x: Double, val y: Double)

operator fun Point.plus(that: Point) = Point(this.x + that.x, this.y + that.y)
operator fun Point.minus(that: Point) = Point(this.x - that.x, this.y - that.y)

private fun pageOrigin(e: Element): Point {
    val box = e.getBoundingClientRect()
    return Point(box.left + window.pageXOffset, box.top + window.pageYOffset)
}

private fun ElementCSSInlineStyle.setPosition(p: Point) {
    style.left = "${p.x}px"
    style.top = "${p.y}px"
}

private class DragState(
    val data: AST,
    val draggedElement: HTMLElement,
    val draggedElementOrigin: Point,
    val gestureOrigin: Point,
    
    var dropTarget: Element? = null
)

private inline fun <reified T : Node> T.deepClone() = cloneNode(true) as T

object DragAndDrop {
    private var dragState: DragState? = null
    
    private fun startDragging(target: Element, pageX: Double, pageY: Double) =
        startDragging(target, Point(pageX, pageY))
    
    private fun startDragging(target: Element, startPosition: Point): Boolean {
        if (dragState != null) {
            dragState?.draggedElement?.remove()
        }
        
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
            gestureOrigin = startPosition)
        
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
    
    fun drop() {
        val dragState = this.dragState ?: return
        val dropTarget = dragState.dropTarget
        val draggedElement = dragState.draggedElement
        
        val isDropped =
            if (dropTarget != null) {
                val dropDetail = DropDetail(dragState.data)
                dropTarget.dispatchEvent(DropEvent(dropDetail))
                dropDetail.accepted
            }
            else {
                false
            }
        
        if (isDropped) {
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
        console.log("body mouse down", ev)
        ev as MouseEvent
        if (startDragging(ev.target as HTMLElement, ev.pageX, ev.pageY)) {
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
    
    fun bodyMouseUp(ev: Event) {
        ev as MouseEvent
        if (dragState != null) {
            ev.preventDefault()
            document.body?.removeEventListener("mousemove", ::bodyMouseDrag, true)
            drop()
        }
    }
    
    fun activate() {
        val body = document.body ?: return
        body.addEventListener("mousedown", ::bodyMouseDown)
        body.addEventListener("mouseup", ::bodyMouseUp)

//        body.addEventListener("touchstart", bodyTouchStart)
//        body.addEventListener("touchend", bodyTouchEnd)
//        body.addEventListener("touchcancel", bodyTouchEnd)
    }
    
    fun bind(dragSourceElement: Element, dataProviderFn: () -> AST) {
        dragSourceElement.addEventListener("carddragstart", { ev: Event ->
            ev as CustomEvent
            val sourceElement = ev.currentTarget as HTMLElement
            val detail = ev.detail as DragStartDetail
            
            detail.element = sourceElement
            detail.elementOrigin = pageOrigin(sourceElement)
            detail.data = dataProviderFn()
        })
    }
}

/*

	function touchWithId(touches, id) {
		return _.find(touches, function(t) {return t.identifier === id;});
	}
	
	function bodyTouchStart(ev) {
		var touch = ev.changedTouches[0];
		if (startDragging(touch.target, touch.pageX, touch.pageY)) {
			ev.preventDefault();
			drag_state.touch_id = touch.identifier;
			document.body.addEventListener("touchmove", bodyTouchDrag, true);
		}
	}

	function bodyTouchDrag(ev) {
		if (drag_state) {
			var touch = touchWithId(ev.changedTouches, drag_state.touch_id);
			if (touch) {
				ev.preventDefault();
				dragTo(touch.pageX, touch.pageY);
			}
		}
	}

	function bodyTouchEnd(ev) {
		if (drag_state) {
			var touch = touchWithId(ev.changedTouches, drag_state.touch_id);
			if (touch) {
				ev.preventDefault();
				document.body.removeEventListener("touchmove", bodyTouchDrag, true);
				drop();
			}
		}
	}
	
	
	return {
		
		// Because I can't safely add methods or properties to CustomEvent
		// but don't want to expose its structure.
		
		data: function(event) {
			return event.detail.data;
		},
		accept: function(event, accepted) {
			event.detail.accepted = accepted || _.isUndefined(accepted);
		}
	};
});

 */