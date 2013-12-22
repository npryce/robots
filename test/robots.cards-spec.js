define(["robots.cards", "underscore", "chai", "fake-context"], function(cards, _, chai, FakeContext) {
	'use strict';
    
	var assert = chai.assert;
    
    function action(name) {
		return new cards.ActionCardStack({action:name, text:name.toUpperCase()}).newCard();
	}
	
    function program() {
		var p = cards.newProgram();
		for (var i = 0; i < arguments.length; i++) {
			p.append(arguments[i]);
		}
		return p;
	}

    function repeat(n, body) {
		var r = new cards.RepeatCardStack({repeat:n}).newCard();
		for (var i = 0; i < body.length; i++) {
			r.append(body[i]);
		}
		return r;
	}

    function run(p, context) {
        p.run(context, function(){context.done();});
	}
	
    function assertElementsEqual(a1, a2, path) {
		assert(_.isArray(a1) && _.isArray(a2), path + ": should both be arrays");
		assert.equal(a1.length, a2.length, path + ".length");
		
		for (var i = 0; i < a1.length; i++) {
			var e1 = a1[i];
			var e2 = a1[i];
			var epath = path + "[" + i + "]";
			
			if (_.isArray(e1)) {
				assertElementsEqual(e1, e2, epath);
			}
			else {
				assert.equal(e1, e2, epath);
			}
		}
	}
	
	describe("Interpretation", function() {
        it("empty program calls 'done' callback immediately", function() {
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

    describe("a CardSequence", function() {
		it("initially has one empty row", function() {
			var s = new cards.CardSequence();
			   
			assert.equal(s.rowcount(), 1);
			assertElementsEqual(s.row(0), [], "first row should be empty");
			assertElementsEqual(s.toArray(), [[]], "s.toArray");
        });
        
        it("creates the first row when the first card is added", function() {
			var s = new cards.CardSequence();
			var a = action("a");

            s.append(a);
			
            assert.equal(s.rowcount(), 1, "rowcount");
			assertElementsEqual(s.row(0), [a], "first row");
			assertElementsEqual(s.toArray(), [[a]], "s.toArray");
        });
		
        it("appends further action cards to the end of the first row when there is one row", function() {
			var s = new cards.CardSequence();
			var a = action("a");
			var b = action("b");
            
            s.append(a);
            s.append(b);
			
            assert.equal(s.rowcount(), 1, "rowcount");
			assertElementsEqual(s.row(0), [a, b], "first row");
			assertElementsEqual(s.toArray(), [[a, b]], "toArray");
        });
	    
        it("appends a card to a new row if the last card of the last row is a control card", function() {
			var s = new cards.CardSequence();
			var a = action("a");
			var r = repeat(2, []);
			var c = action("c");
            
            s.append(a);
			s.append(r);
            s.append(c);
            
            assert.equal(s.rowcount(), 2, "rowcount");
		    assertElementsEqual(s.row(0), [a, r]);
			assertElementsEqual(s.row(1), [c]);
		});

		it("marks a row as closed if the last card if the row is a control card", function() {
			var s = new cards.CardSequence();
			
			s.append(action("a"));
            assert(!s.row(0).closed, "row should not be closed with one action");

            s.append(action("b"));
			assert(!s.row(0).closed, "row should not not be closed with two actions");

            s.append(repeat(2, [action("c")]));
            assert(s.row(0).closed, "row should be closed with control card at end");
   
		});
		
        it("relates rows to the sequence they are part of", function() {
			var s = new cards.CardSequence();
            s.append(action("a"));
			s.append(repeat(2, [action("b")]));
            s.append(action("c"));
			
			_.forEach(s.toArray(), function(row) {
				assert.equal(row.sequence, s);
            });
		});
    });
    
    describe("Total Card Count Calculation", function() {
		it("counts action cards as 1", function(){
			assert.equal(action("a").totalCardCount(), 1);
		});
        it("counts total cards in sequence", function() {
			assert.equal(program(action("a"), action("b"), action("c")).totalCardCount(), 3);
        });
        it("counts total cards in tree of cards", function() {
			var p = program(action("a"), repeat(2, [action("b"), action("c")]), action("d"));
			assert.equal(p.totalCardCount(), 5);
        });
    });
});

