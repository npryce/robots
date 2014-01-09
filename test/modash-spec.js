define(["modash", "chai"], function(_, chai) {
	'use strict';
	var assert = chai.assert;
	
	function isOdd(v) {
		return v % 2 == 1;
	}
	
	describe("splitAfter", function() {
		it("returns no groups for an empty sequence", function() {
			assert.deepEqual(_.splitAfter([], isOdd), []);
		});
		
		it("splits into groups after a value matches a predicate", function() {
			assert.deepEqual(_.splitAfter([0,1,2,3,4,5,6], isOdd), [[0,1],[2,3],[4,5],[6]]);
		});
		
		it("adjacent matching values results in singleton groups", function() {
			assert.deepEqual(_.splitAfter([0,1,3,5,6], isOdd), [[0,1],[3],[5],[6]]);
		});
		
		it("final group can end in a matching value", function() {
			assert.deepEqual(_.splitAfter([0,1,2,3,4,5], isOdd), [[0,1],[2,3],[4,5]]);
		});
	});
});