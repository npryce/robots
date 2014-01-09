define(["lodash", "chai", "robots.undo"], function(_, chai, UndoStack) {
	'use strict';
	
	var assert = chai.assert;
	
	
	describe("UndoStack", function() {
		it("must be created with initial state", function() {
			var u = new UndoStack(1);
			
			assert(u.current() === 1, "initial state");
			assert(!u.canUndo(), "cannot undo");
			assert(!u.canRedo(), "cannot redo");
		});
		
		it("can undo back to previous state after pushing a new state", function() {
			var u = new UndoStack(1);
			u.push(2);
			
			assert(u.current() === 2, "new state");
			assert(u.canUndo(), "can undo");
			assert(!u.canRedo(), "cannot redo");
			
			u.undo();
			
			assert(u.current() === 1, "initial state");
			assert(!u.canUndo(), "cannot undo");
			assert(u.canRedo(), "can redo");
		});
		
		it("can redo back to subsequent state", function() {
			var u = new UndoStack(1);
			u.push(2);
			u.push(3);
			
			u.undo();
			u.undo();
			
			assert(u.current() === 1, "initial state");
			assert(u.canRedo(), "can redo");
			
			u.redo();
			assert(u.current() === 2, "2nd state");
			assert(u.canRedo(), "can redo");

			u.redo();
			assert(u.current() === 3, "final state");
			assert(!u.canRedo(), "cannot redo");
		});
    });
});
    
