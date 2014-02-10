define(["robots.cards", "lodash", "chai", "fake-context"], function(cards, _, chai, FakeContext) {
	'use strict';
    
	var assert = chai.assert;
    
    function action(name, id) {
		return cards.newCard({action:name, title: name, eval:cards.eval.action}, id || name);
	}
	
    function program() {
		return _.toArray(arguments);
	}
	
    function repeat(n) {
		var body, id;
		
		if (arguments.length == 2) {
			body = arguments[1];
			id = 'id';
		}
		else {
			id = arguments[1];
			body = arguments[2];
		}
		
		var card = cards.newCard(cards.repeat[n-2], id); // there are no repeat cards for 0 or 1 iterations
		card.body = body;
		return card;
	}
	
    function run(program, context) {
        cards.run(program, context, function(){context.done();});
	}
	
	describe("Interpretation", function() {
        it("calls 'done' callback immediately if program is empty", function() {
			var context = new FakeContext();
            var p = program();
			
			assert(!context.isDone);
		    run(p, context);
			assert(context.isDone);
        });
		
        it("runs single action", function() {
		    var context = new FakeContext();
			
			var p = program(action("a"));
			
		    run(p, context);
			
		    assert.deepEqual(context.played, ["a"]);
		    assert(context.isDone);
		});
		
        it("runs sequence of actions", function() {
		    var context = new FakeContext();
			
			var p = program(
				action("a"), 
				action("b"),
				action("c"),
				action("d"));
			
		    run(p, context);
			
		    assert.deepEqual(context.played, ["a", "b", "c", "d"]);
		    assert(context.isDone);
		});
				 
		describe("Repetition", function() {
			it("runs repeated actions", function() {
				var context = new FakeContext();
				var p = program(
					action("a"),
					repeat(2, [action("b"), action("c")]),
					action("d"));
			   
				run(p, context);
				
				assert.deepEqual(context.played, [
					"a", 
					"b", "c", "b", "c", 
					"d"]);
				assert(context.isDone);
		    });
	        
            it("runs repeated repeated actions", function() {
	            var context = new FakeContext();
                var p = program(
				    action("a"),
				    repeat(2, [action("b"), repeat(3, [action("c")])]),
				    action("d"));

			    run(p, context);

			    assert.deepEqual(context.played, [
									 "a", 
									 "b", "c", "c", "c", 
									 "b", "c", "c", "c", 
									 "d"]);
			});
			
	        it("allows repeated actions with an empty body", function() {
		        var context = new FakeContext();
                var p = program(
					action("a"),
					repeat(2, []),
					action("d"));
				   
			    run(p, context);
			
		        assert.deepEqual(context.played, ["a", "d"]);
		        assert(context.isDone);
	        });

	        it("annotates the card on each iteration", function() {
				var context = new FakeContext();
				var a = action("a");
				var r = repeat(2, "r", [a]);
                var p = program(r);
				
			    run(p, context);
				   
		        assert.deepEqual(context.trace, [
					{event: 'activate', card_id: "r"},
					{event: 'annotate', card_id: "r", annotation: 2},
					{event: 'activate', card_id: "a"},
					{event: 'action', action: "a", description: "a"},
					{event: 'deactivate', card_id: "a"},
					{event: 'annotate', card_id: "r", annotation: 1},
					{event: 'activate', card_id: "a"},
					{event: 'action', action: "a", description: "a"},
					{event: 'deactivate', card_id: "a"},
					{event: 'deactivate', card_id: "r"},
					{event: 'done'}
				]);
		        assert(context.isDone);
	        });
        });
    });
	
    describe("Total Card Count Calculation", function() {
		it("counts action cards as 1", function(){
            var p = program(action("a"));
            
			assert.equal(cards.programSize(p), 1);
		});
        
		it("counts total cards in sequence", function() {
			var p = program(action("a"), action("b"), action("c"));
			
			assert.equal(cards.programSize(p), 3);
        });
		
        it("counts total cards in tree of cards", function() {
			var p = program(action("a"), repeat(2, [action("b"), action("c")]), action("d"));
			
			assert.equal(cards.programSize(p), 5);
        });
		
		it("counts total cards in empty program", function() {
			var p = program();
            
			assert.equal(cards.programSize(p), 0);
		});
		
		it("counts total cards in empty repeat statement", function() {
			var p = program(repeat(2, []));
            
			assert.equal(cards.programSize(p), 1);
		});
		
		it("counts total cards in nested repeats", function() {
			var p = program(repeat(2, [repeat(3, [])]));
			
			assert.equal(cards.programSize(p), 2);
		});
    });
	
    describe("Predefined Cards", function() {
		it("actions have unique names", function() {
			var actions = [];
			
			_.each(cards.action, function(c) {
				assert.notInclude(actions, c.action, "duplicate action name: " + c.action);
				actions.push(c.action);
			});
		});
	});
});
