define(["d3"], function(d3) {
    function newDragEvent(action) {
		return new CustomEvent("carddrag", {detail: {action: action, handler: null}});
	}

    function removeElement(e) {
		d3.select(e || this).remove();
	}
	
    function newCardGesture(new_card_callback) {
		var dragged_element = null;
		var drop_handler = null;
		
        var drag = d3.behavior.drag();
		
		function origin(e) {
			var bounds = e.getBoundingClientRect();
			return {x: bounds.left, y: bounds.top};
		}
		
		drag.origin(function(stack) {
			return origin(this);
		});
		drag.on("dragstart", function(stack) {
            drop_handler = null;
			dragged_element = this.cloneNode();
			dragged_element.classList.add("dragging");
			document.body.appendChild(dragged_element);
		});
		drag.on("drag", function(stack) {
			var ev = d3.event;
			
			dragged_element.style.left = ev.x + "px";
			dragged_element.style.top = ev.y + "px";
			
			var under = document.elementFromPoint(ev.sourceEvent.pageX, ev.sourceEvent.pageY);
			if (under != null) {
				var dragEvent = newDragEvent("new");
				under.dispatchEvent(dragEvent);
				drop_handler = dragEvent.detail.handler;
			}
		});
		drag.on("dragend", function(stack) {
			if (drop_handler != null) {
				removeElement(dragged_element);
				drop_handler(stack);
			}
			else {
				d3.select(dragged_element)
					.classed("disappearing", true)
					.on("transitionend", removeElement);
			}
			
			dragged_element = null;
        });
		
		return drag;
    }
    
	return {
		newCard: newCardGesture
	};
});
