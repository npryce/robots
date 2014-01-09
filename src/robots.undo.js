define(["d3", "lodash"], function(d3, _) {
	"use strict";

	function UndoStack(initial_state) {
		this._history = [initial_state];
		this._current = 0;
	}
	UndoStack.prototype.push = function(new_state) {
		this._history.push(new_state);
		this._current++;
	};
	UndoStack.prototype.current = function() {
		return this._history[this._current];
	};
	UndoStack.prototype.canUndo = function() {
		return this._current > 0;
	};
	UndoStack.prototype.undo = function() {
		this._current--;
	};
	UndoStack.prototype.canRedo = function() {
		return this._current < (this._history.length-1);
	};
	UndoStack.prototype.redo = function() {
		this._current++;
	};
	
	return UndoStack;
});