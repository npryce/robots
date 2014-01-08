define(["robots.cards2", "lodash", "chai", "fake-context"], function(cards, _, chai, FakeContext) {
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
	
    function run(sequence, context) {
        cards.eval.sequence(sequence, context, function(){context.done();});
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

	describe("Cursor", function() {
		var p = program(action("a"), repeat(2, [action("b1"), action("b2")]), action("c"));
		var c = cards.cursor(p);
		
		it("traverses top-level cards in a program", function() {
			assert(c.canDown(), "can go down to first child of p");
			
			var c0 = c.down();
			
			assert(c0.canRight(), "can go right from first p[0]");
			assert(!c0.canLeft(), "cannot go left from p[0]");
			
			assert(c0.right().canRight(), "can go right from p[1]");
			assert(c0.right().canLeft(), "can go left from p[1]");
			assert.deepEqual(c0.right().left(), c0, "c0->c1->c0");
			
			assert(!c0.right().right().canRight(), "cannot go right from p[2]");
			assert(c0.right().right().canLeft(), "can go left from p[2]");
			assert.deepEqual(c0.right().right().left(), c0.right(), "c0->c1->c2->c1");
        });
		
		it("descends into branches of compound cards", function() {
			var cr = c.down().right();
			
			assert(cr.canDown(), "can descend into branches of c[1]");
			var cb0 = cr.down();
			
			assert(!cb0.canLeft(), "at start of branches");
			assert(!cb0.canRight(), "only one branch");
			assert(cb0.canDown(), "branch contains cards");
			
			var cb0a = cb0.down();
			assert(!cb0a.canLeft(), "at start of branch");
			assert(cb0a.canRight(), "more than one card");
			assert(!cb0a.right().canRight(), "two cards in branch");
		});
		
		it("cannot descend into branches of atomic cards", function() {
			assert(!c.down().canDown(), "cannot descend into c[0]");
		});
		
		it("can zip the program back up again", function() {
			var p2 = c.down().right().down().down().right().top().node;
			
			assert.deepEqual(p2, p);
		});
	});
});
