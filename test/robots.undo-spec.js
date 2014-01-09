define(["lodash", "chai", "robots.undo"], function(_, chai, UndoStack) {
	'use strict';
	
	var assert = chai.assert;
	
	
	describe("UndoStack", function() {
		var initial = UndoStack.startingWith(1);
		
		it("must be created with initial state", function() {
			assert.equal(initial.current(), 1, "initial state");
			assert(!initial.canUndo(), "cannot undo");
			assert(!initial.canRedo(), "cannot redo");
		});
		
		it("can undo back to previous state after pushing a new state", function() {
			var u2 = initial.push(2);
			
			assert.equal(u2.current(), 2, "new state");
			assert(u2.canUndo(), "can undo");
			assert(!u2.canRedo(), "cannot redo");
			
			var u1 = u2.undo();
			assert.equal(u1.current(), 1, "initial state");
			assert(!u1.canUndo(), "cannot undo");
			assert(u1.canRedo(), "can redo");
		});
		
		it("can redo to a subsequent state", function() {
			var u1 = initial.push(2).push(3).undo().undo();
			
			assert.equal(u1.current(), 1, "at initial state");
			assert(u1.canRedo(), "can redo");
			
			var u2 = u1.redo();
			assert(u2.current() === 2, "2nd state");
			assert(u2.canRedo(), "can redo");

			var u3 = u2.redo();
			assert(u3.current() === 3, "final state");
			assert(!u3.canRedo(), "cannot redo");
		});
		
		it("discards redoable states when state is pushed after undoing", function() {
			var u = initial.push(2).push(3).push(4).undo().undo();
			assert(u.canRedo(), "can redo");
			assert.equal(u.current(), 2, "current state");
			
			var t = u.push(99);
			assert.equal(t.current(), 99, "new current state");
			assert(!t.canRedo());
		});
    });
});
