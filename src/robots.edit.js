define(["modash", "robots.cards2"], function(_, cards) {
	'use strict';
	
	
	function EditPoint(sequence, index, parent_edit_point, sequence_name) {
		this.sequence = sequence;
		this.index = index;
		this.parent = parent_edit_point;
		this.sequence_name = sequence_name;
	}
	EditPoint.prototype.node = function() {
		return this.sequence[this.index];
	};
	EditPoint.prototype.insertBefore = function(new_node) {
		return spliceIntoParent(this, this.index, this.index, new_node);
	};
	EditPoint.prototype.insertAfter = function(new_node) {
		return spliceIntoParent(this, this.index+1, this.index+1, new_node);
	};
	EditPoint.prototype.replaceWith = function(new_node) {
		return spliceIntoParent(this, this.index, this.index+1, new_node);
	};
	EditPoint.prototype.editorsForBranch = function(branch_name) {
		return editorsFor(this.node()[branch_name], this, branch_name);
	};
	EditPoint.prototype.appenderForBranch = function(branch_name) {
		return appenderFor(this.node()[branch_name], this, branch_name);
	};
	
	// TODO - make iterative because JavaScript does not have TCO
	function spliceIntoParent(edit_point, index1, index2, new_node) {
		var new_sequence = edit_point.sequence.slice(0,index1).concat([new_node], edit_point.sequence.slice(index2));
		var parent = edit_point.parent;
		
		if (!_.isUndefined(parent)) {
			return parent.replaceWith(cards.setPropertyOf(parent.node(), edit_point.sequence_name, new_sequence));
		}
		else {
			return new_sequence;
		}
	}
	
	function editorsFor(sequence, parent_edit_point, sequence_name) {
		return _.map(_.range(sequence.length), function(i) {
			return new EditPoint(sequence, i, parent_edit_point, sequence_name);
		});
	}
	
	function appenderFor(sequence, parent_edit_point, sequence_name) {
		return new EditPoint(sequence, sequence.length, parent_edit_point, sequence_name);
	}
	

	
	function UndoStack(prevs, current, nexts) {
		this._prevs = prevs;
		this._current = current;
		this._nexts = nexts;
	}
	UndoStack.prototype.current = function() {
		return this._current;
	};
	UndoStack.prototype.push = function(new_state) {
		return new UndoStack([this._prevs, this._current], new_state, null);
	};
	UndoStack.prototype.canUndo = function() {
		return this._prevs != null;
	};
	UndoStack.prototype.undo = function() {
		return new UndoStack(this._prevs[0], this._prevs[1], [this._current, this._nexts]);
	};
	UndoStack.prototype.canRedo = function() {
		return this._nexts != null;
	};
	UndoStack.prototype.redo = function() {
		return new UndoStack([this._prevs, this._current], this._nexts[0], this._nexts[1]);
	};
	
	function undoStartingWith(initial_state) {
		return new UndoStack(null, initial_state, null);
	};
	
	return {
		editorsFor: editorsFor,
		appenderFor: appenderFor,
		undoStartingWith: undoStartingWith
	};

});
