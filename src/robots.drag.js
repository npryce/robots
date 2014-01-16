define(["d3", "lodash"], function(d3, _) {
    function newDragStartEvent() {
		return new CustomEvent("carddragstart", {detail: {data: null}});
	}
    function newDragInEvent(data) {
		return new CustomEvent("carddragin", {detail: {accepted: false}});
	}
    function newDragEvent(data) {
		return new CustomEvent("carddrag", {detail: {accepted: true}});
	}
    function newDragOutEvent(data) {
		return new CustomEvent("carddragout");
	}
    function newDropEvent(data) {
		return new CustomEvent("carddrop", {detail: {data: data}});
	}
	
    function removeElement(e) {
		d3.select(e || this).remove();
	}
	
    function gesture() {
        var drag = d3.behavior.drag();
		var dragged_data = null;
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
			dragged_data = null;
			if (dragged_element != null) {
				removeElement(dragged_element);
			}
			
			var start_event = newDragStartEvent();
			this.dispatchEvent(start_event);
			
			dragged_data = start_event.detail.data;
			if (dragged_data) {
				dragged_element = this.cloneNode(true);
				dragged_element.classList.add("dragging");
				document.body.appendChild(dragged_element);
			}
		});
		drag.on("drag", function() {
			if (dragged_data === null) return;
			
			var ev = d3.event;
			
			dragged_element.style.left = ev.x + "px";
			dragged_element.style.top = ev.y + "px";
			
			var under = document.elementFromPoint(ev.sourceEvent.pageX, ev.sourceEvent.pageY);
			if (under !== drop_target) {
				if (drop_target !== null) {
					drop_target.dispatchEvent(newDragOutEvent(dragged_data));
					drop_target = null;
				}
				if (under !== null) {
					var drag_in_event = newDragInEvent(dragged_data);
					under.dispatchEvent(drag_in_event);
					if (drag_in_event.detail.accepted) {
						drop_target = under;
					}
				}
			}
			else if (under !== null) { 
				// under will be null while dragging outside the browser window
				
				var drag_event = newDragEvent(dragged_data);
				under.dispatchEvent(drag_event);
				if (!drag_event.detail.accepted) {
					drop_target = null;
				}
			}
		});
		drag.on("dragend", function() {
			if (dragged_data === null) return;
			
			if (drop_target != null) {
				removeElement(dragged_element);
				drop_target.dispatchEvent(newDropEvent(dragged_data));
			}
			else {
				d3.select(dragged_element)
					.classed("disappearing", true)
					.on("transitionend", removeElement);
			}
			
			dragged_element = null;
			drop_target = null;
        });
		
		return {
			bind: function(drag_source_element, data_provider_fn) {
				drag_source_element.addEventListener("carddragstart", function(ev) {
					ev.detail.data = data_provider_fn();
				});
				d3.select(drag_source_element).call(drag);
			}
		};
    }
    
	return {
		gesture: gesture,
		
		// Because I can't safely add methods or properties to CustomEvent 
		// but don't want to expose its structure.
		
		data: function(event) {
			return event.detail.data;
		},
		acceptDrop: function(event, accepted) {
			event.detail.accepted = accepted || _.isUndefined(accepted);
		}
	};
});
