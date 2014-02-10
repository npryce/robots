define(["lodash"], function(_) {
	var initialised = false;
	var drag_state = null;
	var dragged_data = null;
	var dragged_element = null;
	
    function newDragStartEvent() {
		return new CustomEvent("carddragstart", {bubbles: true, detail: {data: null}});
	}
    function newDragInEvent(data) {
		return new CustomEvent("carddragin", {bubbles: true, detail: {accepted: false}});
	}
    function newDragOutEvent(data) {
		return new CustomEvent("carddragout", {bubbles: true});
	}
    function newDropEvent(data) {
		return new CustomEvent("carddrop", {bubbles: true, detail: {data: data, accepted:true}});
	}
	
    function removeElement(e) {
		(e || this).remove();
	}
	
	function pageOrigin(e) {
		var box = e.getBoundingClientRect();
		var scrollLeft = window.pageXOffset;
		var scrollTop = window.pageYOffset;
		
		var left = box.left + scrollLeft;
		var top  = box.top +  scrollTop;
		
		return [left, top];
	}
	
	function setElementPosition(e, coords) {
		e.style.left = coords[0] + "px";
		e.style.top = coords[1] + "px";
	}
	
	
	function startDragging(target, page_x, page_y) {
		var start_event = newDragStartEvent();
		
		dragged_data = null;
		if (dragged_element != null) {
			removeElement(dragged_element);
		}
		
		target.dispatchEvent(start_event);
		
		dragged_data = start_event.detail.data;
		if (dragged_data) {
			var source_element = start_event.detail.element;
			var source_pos = start_event.detail.element_origin;
			
			dragged_element = source_element.cloneNode(true);
			dragged_element.classList.add("dragging");
			setElementPosition(dragged_element, source_pos);
			
			document.body.appendChild(dragged_element);
			
			drag_state = {
				data: start_event.detail.data,
				dragged_element: dragged_element,
				dragged_element_origin: source_pos,
				gesture_origin: [page_x, page_y],
				drop_target: null
			};
			
			return true;
		}
		else {
			return false;
		}
	}
	
    function dragTo(page_x, page_y) {
		setElementPosition(dragged_element, [
			drag_state.dragged_element_origin[0] + (page_x - drag_state.gesture_origin[0]),
			drag_state.dragged_element_origin[1] + (page_y - drag_state.gesture_origin[1])]);
		
		var under = document.elementFromPoint(page_x, page_y);
		if (under !== drag_state.drop_target) {
			if (drag_state.drop_target !== null) {
				drag_state.drop_target.dispatchEvent(newDragOutEvent(dragged_data));
				drag_state.drop_target = null;
			}
			
			if (under !== null) {
				var drag_in_event = newDragInEvent(dragged_data);
				under.dispatchEvent(drag_in_event);
				if (drag_in_event.detail.accepted) {
					drag_state.drop_target = under;
				}
			}
		}
	}
	
    function drop() {
		var dragged_element = drag_state.dragged_element;
		var dropped;
		
		if (drag_state.drop_target) {
			var drop_event = newDropEvent(drag_state.data);
			drag_state.drop_target.dispatchEvent(drop_event);
			dropped = drop_event.detail.accepted;
		}
		else {
			dropped = false;
		}
		
		if (dropped) {
			removeElement(dragged_element);
		} else {
			drag_state.dragged_element.classList.add("disappearing");
			drag_state.dragged_element.addEventListener("transitionend", function() {removeElement(dragged_element);});
		}
		
		drag_state = null;
	}
	
	function bodyMouseDown(ev) {
		if (startDragging(ev.target, ev.pageX, ev.pageY)) {
			ev.preventDefault();
			document.body.addEventListener("mousemove", bodyMouseDrag, true);
		}
	}
	
	function bodyMouseDrag(ev) {
		if (drag_state) {
			ev.preventDefault();
			dragTo(ev.pageX, ev.pageY);
		}
	}

	function bodyMouseUp(ev) {
		if (drag_state) {
			ev.preventDefault();
			document.body.removeEventListener("mousemove", bodyMouseDrag, true);
			drop();
		}
	}
	

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
	
	function initDragAndDrop() {
		var body = document.body;
		body.addEventListener("mousedown", bodyMouseDown);
		body.addEventListener("mouseup", bodyMouseUp);
		
		body.addEventListener("touchstart", bodyTouchStart);
		body.addEventListener("touchend", bodyTouchEnd);
		body.addEventListener("touchcancel", bodyTouchEnd);
		
		initialised = true;
	}
	
	initDragAndDrop();
	
	return {
		bind: function(drag_source_element, data_provider_fn) {
			drag_source_element.addEventListener("carddragstart", function(ev) {
				ev.detail.element = ev.currentTarget;
				ev.detail.element_origin = pageOrigin(ev.currentTarget);
				ev.detail.data = data_provider_fn();
			});
		},
		
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
