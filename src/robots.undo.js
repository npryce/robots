define(["d3", "lodash", "js-tree-cursor"], function(d3, _, treecursor) {
	"use strict";
	
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
	
	UndoStack.startingWith = function(initial_state) {
		return new UndoStack(null, initial_state, null);
	};
	
	return UndoStack;
});