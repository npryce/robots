define(["d3", "lodash", "js-tree-cursor"], function(d3, _, treecursor) {
	"use strict";
	
	function clearRight(cursor) {
		return new TreeCursor(
			cursor.parent,
			cursor.node,
			cursor.prevs,
			[],
			cursor.openF,
			cursor.closeF,
			cursor.atomicF);
	}
	
	function UndoStack(cursor) {
		this._cursor = cursor;
	}
	UndoStack.prototype.current = function() {
		return this._cursor.node;
	};
	UndoStack.prototype.push = function(new_state) {
		return new UndoStack(clearRight(this._cursor).insertRight(new_state));
	};
	UndoStack.prototype.canUndo = function() {
		return this._cursor.canLeft();
	};
	UndoStack.prototype.undo = function() {
		return new UndoStack(this._cursor.left());
	};
	UndoStack.prototype.canRedo = function() {
		return this._cursor.canRight();
	};
	UndoStack.prototype.redo = function() {
		return new UndoStack(this._cursor.right());
	};
	
	UndoStack.startingWith = function(initial_state) {
		return new UndoStack(treecursor.arrayToCursor([initial_state]));
	};
	
	return UndoStack;
});