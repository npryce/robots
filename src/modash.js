define(["lodash"], function(_) {
	'use strict';
	
	function splitAfter(seq, p) {
		var groups = [];
		var current_group = [];
		
		_.each(seq, function(e) {
			current_group.push(e);
			if (p(e)) {
				groups.push(current_group);
				current_group = [];
			}
		});
	
		if (current_group.length !== 0) {
			groups.push(current_group);
		}
		
		return groups;
	}
	
	

	_.mixin({
		splitAfter: splitAfter
	});
	
	return _;
});
