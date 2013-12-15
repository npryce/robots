define(["d3"], function(d3) {
    function newDragInEvent(action) {
		return new CustomEvent("carddragin", {detail: {action: action, accepted: false}});
	}
    function newDragEvent(action) {
		return new CustomEvent("carddrag", {detail: {action: action, accepted: true}});
	}
    function newDragOutEvent(action) {
		return new CustomEvent("carddragout");
	}
    function newDropEvent(action, data) {
		return new CustomEvent("carddrop", {detail: {action: action, data: data}});
	}
	
    function removeElement(e) {
		d3.select(e || this).remove();
	}
	
    function newCardGesture() {
		var drop_action = "new";
		
        var drag = d3.behavior.drag();
		var dragged_element = null;
		var drop_target = null;

		function origin(e) {
			var bounds = e.getBoundingClientRect();
			return {x: bounds.left, y: bounds.top};
		}
		
		drag.origin(function(stack) {
			return origin(this);
		});
		drag.on("dragstart", function(stack) {
            drop_target = null;
			dragged_element = this.cloneNode();
			dragged_element.classList.add("dragging");
			document.body.appendChild(dragged_element);
		});
		drag.on("drag", function(stack) {
			var ev = d3.event;
			
			dragged_element.style.left = ev.x + "px";
			dragged_element.style.top = ev.y + "px";
			
			var under = document.elementFromPoint(ev.sourceEvent.pageX, ev.sourceEvent.pageY);
			if (under !== drop_target) {
				if (drop_target !== null) {
					drop_target.dispatchEvent(newDragOutEvent(drop_action));
				}
				if (under != null) {
					var drag_in_event = newDragInEvent(drop_action);
					under.dispatchEvent(drag_in_event);
					if (drag_in_event.detail.accepted) {
						drop_target = under;
					}
				}
			}
			else {
				var drag_event = newDragEvent(drop_action);
				under.dispatchEvent(drag_event);
				if (!drag_event.detail.accepted) {
					drop_target = null;
				}
			}
		});
		drag.on("dragend", function(stack) {
			if (drop_target != null) {
				removeElement(dragged_element);
				drop_target.dispatchEvent(newDropEvent(drop_action, stack));
			}
			else {
				d3.select(dragged_element)
					.classed("disappearing", true)
					.on("transitionend", removeElement);
			}
			
			dragged_element = null;
			drop_target = null;
        });
		
		return drag;
    }
    
	return {
		newCard: newCardGesture
	};
});
