define(["lodash", "chai", "robots.cards", "robots.edit"], function(_, chai, cards, edit) {
	'use strict';
    
	var assert = chai.assert;
    
    function action(name) {
		cards.newCard({action:name, eval:cards.eval.action}, "id");
	}
	
    function program() {
		return _.toArray(arguments);
	}
	
    function repeat(n, body) {
		var card = cards.newCard(cards.repeat[n-2], "id"); // there are no repeat cards for 0 or 1 iterations
		card.body = body;
		return card;
	}
	
	var a = action("a");
	var b = action("b");
	var c = action("c");
	var d = action("d");
	
	describe("Editing", function() {
		describe("Empty Program", function() {
			var empty_program = program();
			var appender = edit.appenderFor(empty_program);
			
			it("can append card at end of an empty program", function() {
				assert.deepEqual(appender.insertBefore(a), program(a));
				assert.deepEqual(appender.insertAfter(a), program(a));
				assert.deepEqual(appender.replaceWith(a), program(a));
			});
			it("ignores remove", function() {
				assert.deepEqual(appender.remove(), empty_program);
			});
		});
		
		describe("Flat Program", function() {
			var p = program(a,b);
			var editors = edit.editorsFor(p);
			
			it("appends editor for point after end of sequence", function() {
				assert.equal(editors.length, 2);
				assert.equal(editors[0].node(), a);
				assert.equal(editors[1].node(), b);
			});
			it("can insert before existing card", function() {
				assert.deepEqual(editors[0].insertBefore(c), program(c, a, b));
				assert.deepEqual(editors[1].insertBefore(c), program(a, c, b));
			});
			it("can insert after existing card", function() {
				assert.deepEqual(editors[0].insertAfter(c), program(a, c, b));
				assert.deepEqual(editors[1].insertAfter(c), program(a, b, c));
			});
			it ("can replace existing card", function() {
				assert.deepEqual(editors[0].replaceWith(c), program(c, b));
				assert.deepEqual(editors[1].replaceWith(c), program(a, c));
			});
			it("can remove existing card", function() {
				assert.deepEqual(editors[0].remove(), program(b));
				assert.deepEqual(editors[1].remove(), program(b));
			});
		});
		
		describe("Nested Program", function() {
			var p = program(a,repeat(2, [b,c]));
			var p_editors = edit.editorsFor(p);
			var repeat_body_editors = p_editors[1].editorsForBranch('body');
			
			it("can return editors for named branch", function() {
				assert.equal(repeat_body_editors.length, 2);
				assert.equal(repeat_body_editors[0].node(), b);
				assert.equal(repeat_body_editors[1].node(), c);
			});
			
			it("can insert before existing card", function() {
				assert.deepEqual(repeat_body_editors[0].insertBefore(d), program(a, repeat(2, [d, b, c])));
				assert.deepEqual(repeat_body_editors[1].insertBefore(c), program(a, repeat(2, [b, d, c])));
			});
			it("can insert after existing card", function() {
				assert.deepEqual(repeat_body_editors[0].insertAfter(d), program(a, repeat(2, [b, d, c])));
				assert.deepEqual(repeat_body_editors[1].insertAfter(d), program(a, repeat(2, [b, c, d])));
			});
			it ("can replace existing card", function() {
				assert.deepEqual(repeat_body_editors[0].replaceWith(d), program(a, repeat(2, [d, c])));
				assert.deepEqual(repeat_body_editors[1].replaceWith(d), program(a, repeat(2, [b, d])));
			});
			it("can remove existing card", function() {
				assert.deepEqual(p_editors[1].remove(), program(a));
				assert.deepEqual(repeat_body_editors[0].remove(), program(a, repeat(2, [c])));
				assert.deepEqual(repeat_body_editors[1].remove(), program(a, repeat(2, [b])));
			});
			
			it("can append to nested branch", function() {
				var repeat_body_appender = p_editors[1].appenderForBranch('body');
				
				assert.deepEqual(repeat_body_appender.insertBefore(d), program(a, repeat(2, [b, c, d])));
				assert.deepEqual(repeat_body_appender.insertAfter(d), program(a, repeat(2, [b, c, d])));
				assert.deepEqual(repeat_body_appender.replaceWith(d), program(a, repeat(2, [b, c, d])));
			});
		});
	});


	describe("Undo Stack", function() {
		var initial = edit.undoStartingWith(1);
		
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
