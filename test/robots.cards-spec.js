
define(["robots.cards", "underscore", "chai"], function(cards, _, chai) {
	'use strict';
    
	var assert = chai.assert;
    
    function FakeContext() {
		this.trace = [];
		this.played = [];
		this.active = {};
		this.isDone = false;
	}
	FakeContext.prototype.play = function(sample_name, onfinished) {
		this.trace.push({event:'play', sample:sample_name});
		this.played.push(sample_name);
		onfinished();
	};
    FakeContext.prototype.activate = function(card_id) {
		this.trace.push({event:'activate', card_id:card_id});
		this.active[card_id] = true;
	};
    FakeContext.prototype.deactivate = function(card_id) {
		this.trace.push({event:'deactivate', card_id:card_id});
		delete this.active[card_id];
	};
	FakeContext.prototype.done = function() {
		this.trace.push({event:'done'});
		this.isDone = true;
	};
    FakeContext.prototype.assertTraceEquals = function(expectedTrace) {
		assert.deepEqual(this.trace, expectedTrace);
	};
    FakeContext.prototype.assertTraceContains = function(event) {
		assert.include(this.trace, event);
	};
	
    var action_a = new cards.ActionCardStack({action: "a", text:"A"}).newCard();
    var action_b = new cards.ActionCardStack({action: "b", text:"B"}).newCard();
    var action_c = new cards.ActionCardStack({action: "c", text:"C"}).newCard();
    var action_d = new cards.ActionCardStack({action: "d", text:"D"}).newCard();
	
    describe("interpretation", function() {
        it("calls 'done' callback immediately when empty", function() {
			var context = new FakeContext();
            var p = cards.newProgram();
			
			assert(!context.isDone);
			p.run(context, function() {context.done();});            
			assert(context.isDone);
        });
		
        it("runs single action", function() {
		    var context = new FakeContext();
			
			var p = cards.newProgram();
			p.append(action_a);
			
            p.run(context, function(){context.done();});
			
		    assert.deepEqual(context.played, ["actions/a"]);
		    assert(context.isDone);
		});
		
        it("runs sequence of actions", function() {
		    var context = new FakeContext();
			
			var p = cards.newProgram();
			p.append(action_a);
			p.append(action_b);
			p.append(action_c);
			p.append(action_d);
			
            p.run(context, function(){context.done();});
			
		    assert.deepEqual(context.played, ["actions/a", "actions/b", "actions/c", "actions/d"]);
		    assert(context.isDone);
		});

		describe("repetition", function() {
		    it("runs repeated actions", function() {
	           var context = new FakeContext();
               var p = cards.newProgram();
			   p.append(action_a);
			   var repeat = new cards.RepeatCardStack({repeat:2}).newCard();
			   repeat.append(action_b);
			   repeat.append(action_c);
			   p.append(repeat);
			   p.append(action_d);
			   
			   p.run(context, function(){context.done();});
				   
			   assert.deepEqual(context.played, ["actions/a", "actions/b", "actions/c", "actions/b", "actions/c", "actions/d"]);
			   assert(context.isDone);
		    });
	   
	        it("allows repeated actions with an empty body", function() {
		        var context = new FakeContext();
                var p = cards.newProgram();
			    p.append(action_a);
			    var repeat = new cards.RepeatCardStack({repeat:2}).newCard();
			    p.append(repeat);
			    p.append(action_d);
			
                p.run(context, function(){context.done();});
			
		        assert.deepEqual(context.played, ["actions/a", "actions/d"]);
		        assert(context.isDone);
	        });
        });
    });
});

