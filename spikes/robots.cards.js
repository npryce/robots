define(["underscore"], function(_) {
    function noop() {
	}
	
    function actionSoundClip(action_name) {
		return "actions/" + action_name;
	}
    
    function ActionCardStack(args) {
		this.action = args.action;
		this.text = args.text;
	}
    ActionCardStack.prototype.preload = function(audio_player) {
		audio_player.load(actionSoundClip(this.action));
	};
    ActionCardStack.prototype.apply = function(audio_player, continuation) {
		audio_player.play(actionSoundClip(this.action), continuation);
	};
	
    function RepeatCardStack(args) {
		this.repeat = args.repeat;
		this.text = args.text;
	}
	RepeatCardStack.prototype.preload = noop;
	RepeatCardStack.apply = noop;
    
	var cards = {
		actions: {
			stepForward: new ActionCardStack({
				action: "step-forward",
				text: "\u21E7"
			}),
			stepBackward: new ActionCardStack({
				action: "step-backward",
				text: "\u21E9"
			}),
			jumpForward: new ActionCardStack({
				action: "jump-forward",
				text: "\u21EA"
			}),
			turnAnticlockwise: new ActionCardStack({
				action: "turn-anticlockwise",
				text: "\u21B6"
			}),
			turnClockwise: new ActionCardStack({
				action: "turn-clockwise",
				text: "\u21B7"
			}),
			pickUp: new ActionCardStack({
				action: "pick-up",
				text: "\u261D"
			}),
			putDown: new ActionCardStack({
				action: "put-down",
				text: "\u261F"
			})
		},
		
		numeric: {
		}
	};
    
    for (var i in [0,1,2,3,4,5,6,7,8,9]) {
		cards.numeric["repeat"+i] = new RepeatCardStack({
			repeat: i,
			text: "" + i
		});
	}
	
    cards.all = function() {
		return _.flatten(_.values(this).map(_.values));
	};
    
    cards.preload = function(audio_player) {
		this.all().forEach(function (card_type) {
		    card_type.preload(audio_player);
		});
	};
    
    return cards;
});
