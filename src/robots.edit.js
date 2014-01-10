define(["modash"], function(_) {
	'use strict';
	
	function cloneReplacing(o, property_name, new_value) {
		var clone = _.create(Object.getPrototypeOf(o));
		
		var replacement = {};
		replacement[property_name] = new_value;
		
		return _.defaults(clone, replacement, o);
	}

	function splice(sequence, index1, index2, newValue) {
		return sequence.slice(0,index1).concat([newValue], sequence.slice(index2));
	}
	
	
	function EditPoint(sequence, index, parent_edit_point, sequence_name) {
		this.sequence = sequence;
		this.index = index;
		this.parent = parent_edit_point;
		this.sequence_name = sequence_name;
	}
	EditPoint.prototype.node = function() {
		return this.sequence[index];
	};
	EditPoint.prototype.insertBefore = function(newNode) {
		return this._newTree(splice(this.sequence, this.index, this.index, newNode));
	};
	EditPoint.prototype.insertAfter = function(newNode) {
		return this._newTree(splice(this.sequence, this.index+1, this.index+1, newNode));
	};
	EditPoint.prototype.replaceWith = function(newNode) {
		return this._newTree(splice(this.sequence, this.index, this.index+1, newNode));
	};
	EditPoint.prototype._newTree = function(new_sequence) {
		var parent = this.parent;
		if (_.isUndefined(parent)) {
			return new_sequence;
		}
		else {
			var new_parent = cloneReplacing(parent.node(), this.sequence_name, new_sequence);
			// TODO
			return null;
		}
	};
	
	
	function editorsFor(sequence, parent_edit_point, sequence_name) {
		return _.map(_.range(sequence.length+1), function(i) {
			return new EditPoint(sequence, i, parent_edit_point, sequence_name);
		});
	}
	
	return {
		editorsFor: editorsFor
	};
});
