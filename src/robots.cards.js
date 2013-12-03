define(["underscore"], function(_) {
    function noop() {
	}
	
    function CardSequence() {
		this.steps = [];
	}
    CardSequence.prototype.run = function(context, onfinished) {
		var next = 0;
		
		var runNextStep = _.bind(function() {
			if (next < this.steps.length) {
				var step = this.steps[next];
				next++;
				context.run(step, runNextStep);
			}
			else {
			    onfinished();
			}
		}, this);
		
		runNextStep();
	};
	CardSequence.prototype.length = function() {
		return this.steps.length;
	};
	CardSequence.prototype.step = function(i) {
		return this.steps[i];
	};
    CardSequence.prototype.toArray = function() {
		return _.toArray(this.steps);
	};
	CardSequence.prototype.append = function(step) {
	    this.steps.push(step);	
	};
    CardSequence.prototype.remove = function(i) {
		this.steps.splice(i, 1);
	};
    CardSequence.prototype.insert = function(i, step) {
		this.steps.splice(i, 0, step);
	};
    CardSequence.prototype.totalCardCount = function() {
		return _(this.steps).map(function(s){return s.totalCardCount();}).reduce(function(a,b){return a+b;}, 0);
	};
    
    
    function CardStack() {
	}
	CardStack.prototype.newCard = function() {
		var card = new this.card(this);
		card.text = this.text;
		card.id = _.uniqueId("card-");
		return card;
	};
    
    
    function ActionCard(stack) {
		this.stack = stack;
	}
	ActionCard.prototype.run = function(context, onfinished) {
		context.play(this.stack.clip(), onfinished);
	};
	ActionCard.prototype.isAtomic = true;
	ActionCard.prototype.contents = function() {
		return [];
	};
    ActionCard.prototype.totalCardCount = function() {
		return 1;
	};
    
    
	function ActionCardStack(args) {
		this.action = args.action;
		this.text = args.text;
	}
	ActionCardStack.prototype = new CardStack();
	ActionCardStack.prototype.card = ActionCard;
    ActionCardStack.prototype.preload = function(audio_player) {
		audio_player.load(this.clip());
	};
    ActionCardStack.prototype.clip = function() {
		return "actions/" + this.action;
	};
    
    
    function RepeatCard(stack) {
		this.stack = stack;
		this.body = new CardSequence();
	}
	RepeatCard.prototype.isAtomic = false;
    RepeatCard.prototype.contents = function() {
		return this.body.toArray();
	};
    RepeatCard.prototype.run = function(context, onfinished) {
        var iteration = 0;
		
        var runNextIteration = _.bind(function() {
		    if (iteration < this.stack.repeat) {
				iteration++;
				this.body.run(context, runNextIteration);
			} else {
				onfinished();
			}
		}, this);
		
		runNextIteration();
	};
	RepeatCard.prototype.append = function(step) {
	    this.body.append(step);
	};
    RepeatCard.prototype.remove = function(i) {
		this.body.remove(i);
	};
    RepeatCard.prototype.insert = function(i, step) {
		this.body.remove(i, step);
	};
	RepeatCard.prototype.totalCardCount = function() {
		return 1 + this.body.totalCardCount();
	};
    
    function RepeatCardStack(args) {
		this.repeat = args.repeat;
		this.text = args.text;
	}
    RepeatCardStack.prototype = new CardStack();
    RepeatCardStack.prototype.card = RepeatCard;
	RepeatCardStack.prototype.preload = noop;
    
	
    
    var cards = {
		newProgram: function() { return new CardSequence(); },
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
		}		
	};
    
    cards.control = {};
    _.forEach([2,3,4,5,6,7,8,9,10], function(i) {
		cards.control["repeat_"+i] = new RepeatCardStack({
			repeat: i,
			text: i.toString()
		});
	});
	
    cards.preload = function(audio_player) {
		_.values(this.actions).forEach(function (card_type) {
		    card_type.preload(audio_player);
		});
	};
    
    return cards;
});
