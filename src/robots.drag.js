define(["d3"], function(d3) {
    function newCardGesture(new_card_callback) {
		var isDragging = false;
		var draggedElement = null;
		var targetElement = null;
		
        var drag = d3.behavior.drag();
		
		function origin(e) {
			var bounds = e.getBoundingClientRect();
			return {x: bounds.left, y: bounds.top};
		}
		
		drag.origin(function(stack) {
			return origin(this);
		});
		drag.on("dragstart", function(stack) {
			isDragging = true;
			draggedElement = this.cloneNode();
			draggedElement.classList.add("dragging");
			document.body.appendChild(draggedElement);
		});
		drag.on("drag", function(stack) {
			var ev = d3.event;
			
			draggedElement.style.left = ev.x + "px";
			draggedElement.style.top = ev.y + "px";
			
			// Todo - make fire namespaced dragover events and move drop action into separate controllers
			var under = document.elementFromPoint(ev.sourceEvent.pageX, ev.sourceEvent.pageY);
			if (under != null) {
				if (d3.select(under).classed("cursor")) {
					targetElement = under;
				}
				else {
					targetElement = null;
				}
			}
		});
		drag.on("dragend", function(stack) {
			isDragging = false;
			document.body.removeChild(draggedElement);
			draggedElement = null;
			
			if (targetElement != null) {
				new_card_callback(stack);
			}
        });
		
		return drag;
    }
    
	return {
		newCard: newCardGesture
	};
});
