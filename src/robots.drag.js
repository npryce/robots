define(["d3", "underscore"], function(d3, _) {
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
	
    function gesture(drop_action) {
        var drag = d3.behavior.drag();
		var dragged_element = null;
		var drop_target = null;

		function origin(e) {
			var bounds = e.getBoundingClientRect();
			return {x: bounds.left, y: bounds.top};
		}
		
		drag.origin(function() {
			return origin(this);
		});
		drag.on("dragstart", function() {
            drop_target = null;
			dragged_element = this.cloneNode();
			dragged_element.classList.add("dragging");
			document.body.appendChild(dragged_element);
		});
		drag.on("drag", function() {
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
		drag.on("dragend", function(data) {
			if (drop_target != null) {
				removeElement(dragged_element);
				drop_target.dispatchEvent(newDropEvent(drop_action, data));
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
		gesture: gesture,
		
		// Because I can't safely add methods or properties to CustomEvent 
		// but don't want to expose its structure.
		action: function(event) {
			return event.detail.action;
		},
		data: function(event) {
			return event.detail.data;
		},
		accept: function(event, flag) {
			event.detail.accepted = flag || _.isUndefined(flag);
		}
	};
});
