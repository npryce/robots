define(["underscore"], function(_) {
	var card_types = {
		actions: {
			stepForward: {
				action: "step-forward",
				text: "\u21E7",
				initiallyEnabled: true
			},
			stepBackward: {
				action: "step-backward",
				text: "\u21E9",
				initiallyEnabled: true
			},
			jumpForward: {
				action: "jump-forward",
				text: "\u21EA",
				initiallyEnabled: true
			},
			turnAnticlockwise: {
				action: "turn-anticlockwise",
				text: "\u21B6",
				initiallyEnabled: true
			},
			turnClockwise: {
				action: "turn-clockwise",
				text: "\u21B7",
				initiallyEnabled: true
			},
			pickUp: {
				action: "pick-up",
				text: "\u261D",
				initiallyEnabled: true
			},
			putDown: {
				action: "put-down",
				text: "\u261F",
				initiallyEnabled: true
			}
		},
		
		numeric: {
		},
		
		inputOnly: {
			newline: {
				name: "newline",
				text: "\u23CE",
				initiallyEnabled: false
			}
		}
	};
    
    for (var i in [0,1,2,3,4,5,6,7,8,9]) {
		card_types.numeric["repeat"+i] = {
			name: "repeat-" + i,
			text: "" + i,
			initiallyEnabled: i > 1
		};
	}
	
    function all() {
		return _.flatten(_.values(this).map(_.values));
	}

    card_types.all = all;
    
    return card_types;
});
