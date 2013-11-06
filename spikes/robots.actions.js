define(["underscore"], function(_) {
	var actions = {	
		stepForward: {
			name: "step-forward",
			text: "\u21E7",
			classes: ["action"],
			initiallyEnabled: true
		},
		stepBackward: {
			name: "step-forward",
			text: "\u21E9",
			classes: ["action"],
			initiallyEnabled: true
		},
		jumpForward: {
			name: "jump-forward",
			text: "\u21EA",
			classes: ["action"],
			initiallyEnabled: true
		},
		turnAnticlockwise: {
			name: "turn-anticlockwise",
			text: "\u21B6",
			classes: ["action"],
			initiallyEnabled: true
		},
		turnClockwise: {
			name: "turn-clockwise",
			text: "\u21B7",
			classes: ["action"],
			initiallyEnabled: true
		},
		pickUp: {
			name: "pick-up",
			text: "\u261D",
			classes: ["action"],
			initiallyEnabled: true
		},
		putDown: {
			name: "put-down",
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
		actions["repeat"+i] = {
			name: "repeat-" + i,
			text: "" + i,
			classes: ["numeric"],
			initiallyEnabled: i > 1
		};
	}
    
    _.each(actions, function(a) {
		a.audio = new Audio("audio/" + a.name + ".mp3");
    }); 
    
    return actions;
});