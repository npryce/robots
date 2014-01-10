define(["lodash", "chai", "robots.cards2", "robots.edit"], function(_, chai, cards, edit) {
	'use strict';
    
	var assert = chai.assert;
    
    function action(name) {
		var card = cards.newCard({action:name, eval:cards.eval.action});
		return card;
	}
	
    function program() {
		return _.toArray(arguments);
	}
	
    function repeat(n, body) {
		var card = cards.newCard(cards.repeat[n-2]); // there are no repeat cards for 0 or 1 iterations
		card.body = body;
		return card;
	}
	
	var a = action("a");
	var b = action("b");
	var c = action("c");
	
	describe("Editing", function() {
		describe("Empty Program", function() {
			var empty_program = program();
			var editors = edit.editorsFor(empty_program);
			
			it("appends editor for point after end of empty program", function() {
				assert.equal(editors.length, 1);
			});
			it("can insert cards at end of an empty program", function() {
				assert.deepEqual(editors[0].insertBefore(a), program(a));
				assert.deepEqual(editors[0].insertAfter(a), program(a));
				assert.deepEqual(editors[0].replaceWith(a), program(a));
			});
		});

		describe("Flat program", function() {
			var p = program(a,b);
			var editors = edit.editorsFor(p);
			
			it("appends editor for point after end of sequence", function() {
				assert.equal(editors.length, 3);
			});
			it("can insert cards at end of program", function() {
				assert.deepEqual(editors[2].insertBefore(c), program(a, b, c));
				assert.deepEqual(editors[2].insertAfter(c), program(a, b, c));
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
				assert.deepEqual(editors[2].replaceWith(c), program(a, b, c));
			});
		});
	});
});
