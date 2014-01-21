define(["robots.cards", "lodash", "chai", "fake-context"], function(cards, _, chai, FakeContext) {
	'use strict';
    
	var assert = chai.assert;
    
    function action(name) {
		return cards.newCard({action:name, eval:cards.eval.action}, 'id');
	}
	
    function program() {
		return _.toArray(arguments);
	}
	
    function repeat(n, body) {
		var card = cards.newCard(cards.repeat[n-2], 'id'); // there are no repeat cards for 0 or 1 iterations
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
			
		    assert.deepEqual(context.played, ["actions/a"]);
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
			
		    assert.deepEqual(context.played, ["actions/a", "actions/b", "actions/c", "actions/d"]);
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
					"actions/a", 
					"actions/b", "actions/c", "actions/b", "actions/c", 
					"actions/d"]);
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
									 "actions/a", 
									 "actions/b", "actions/c", "actions/c", "actions/c", 
									 "actions/b", "actions/c", "actions/c", "actions/c", 
									 "actions/d"]);
			});
			
	        it("allows repeated actions with an empty body", function() {
		        var context = new FakeContext();
                var p = program(
					action("a"),
					repeat(2, []),
					action("d"));
				   
			    run(p, context);
			
		        assert.deepEqual(context.played, ["actions/a", "actions/d"]);
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
    });
});
