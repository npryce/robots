define(["underscore"], function(_) {
	var cards = {
		stepForward: {
			action: "step-forward",
			text: "\u21E7",
			classes: ["action"],
			initiallyEnabled: true
		},
		stepBackward: {
			action: "step-forward",
			text: "\u21E9",
			classes: ["action"],
			initiallyEnabled: true
		},
		jumpForward: {
			action: "jump-forward",
			text: "\u21EA",
			classes: ["action"],
			initiallyEnabled: true
		},
		turnAnticlockwise: {
			action: "turn-anticlockwise",
			text: "\u21B6",
			classes: ["action"],
			initiallyEnabled: true
		},
		turnClockwise: {
			action: "turn-clockwise",
			text: "\u21B7",
			classes: ["action"],
			initiallyEnabled: true
		},
		pickUp: {
			action: "pick-up",
			text: "\u261D",
			classes: ["action"],
			initiallyEnabled: true
		},
		putDown: {
			action: "put-down",
			text: "\u261F",
			classes: ["action"],
			initiallyEnabled: true
		},
		newline: {
			name: "newline",
			text: "",
			classes: "meta",
			initiallyEnabled: false
		}
	};
    
    for (var i in [0,1,2,3,4,5,6,7,8,9]) {
		cards["repeat"+i] = {
			name: "repeat-" + i,
			text: "" + i,
			classes: ["numeric"],
			initiallyEnabled: i > 1
		};
	}
    
    return cards;
});