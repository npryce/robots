define(["lodash", "chai"], function(_, chai) {
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
	
    return FakeContext;
});
