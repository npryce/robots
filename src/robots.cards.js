define(["lodash"], function(_) {
    function noop() {
	}
	
    function addNewRowTo(seq) {
		var new_row = [];
		new_row.sequence = seq;
		new_row.closed = false;
		seq.rows.push(new_row);
	}
    
    function CardSequence() {
		this.rows = [];
		addNewRowTo(this);
	}    
	CardSequence.prototype.rowcount = function() {
		return this.rows.length;
	};
	CardSequence.prototype.row = function(i) {
		return this.rows[i];
	};
    CardSequence.prototype.toArray = function() {
		return _.toArray(this.rows);
	};
	CardSequence.prototype.lastRow = function() {
		return _.last(this.rows);
	};
	CardSequence.prototype.append = function(step) {
		if (this.rows.length == 0 || this.lastRow().closed) {
			addNewRowTo(this);
		}
		
		var row = this.lastRow();
		row.push(step);
		if (!step.isAtomic) {
			row.closed = true;
		}
	};
    CardSequence.prototype.run = function(context, onfinished) {
		var nextRow = 0;
		var nextStep = 0;
		
		var runNextStep = _.bind(function() {
			if (nextRow >= this.rows.length) {
				onfinished();
				return;
			}
			
			var row = this.rows[nextRow];
			if (nextStep >= row.length) {
				nextRow++;
				nextStep = 0;
				runNextStep();
			}
			else {
				var step = row[nextStep];
				nextStep++;
				step.run(context, runNextStep);
			}
		}, this);
		
		runNextStep();
	};
    CardSequence.prototype.totalCardCount = function() {
		return _(this.rows).flatten().map(function(s){return s.totalCardCount();}).reduce(function(a,b){return a+b;}, 0);
	};


    function Card() {
	    this.id = _.uniqueId("card-");	
	}
	Card.prototype.run = function(context, onfinished) {
		var cardId = this.id;
		
		context.activate(cardId);
		this.behaviour(context, function() {
			context.deactivate(cardId);
			onfinished();
		});
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
		Card.call(this);
		this.stack = stack;
	}
    ActionCard.prototype = new Card();
	ActionCard.prototype.behaviour = function(context, onfinished) {
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
		Card.call(this);
		this.stack = stack;
		this.body = new CardSequence();
	}
	RepeatCard.prototype = new Card();
	RepeatCard.prototype.isAtomic = false;
    RepeatCard.prototype.contents = function() {
		return this.body.toArray();
	};
    RepeatCard.prototype.behaviour = function(context, onfinished) {
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
		this.text = args.repeat.toString();
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
			repeat: i
		});
	});
	
    cards.preload = function(audio_player) {
		_.values(this.actions).forEach(function (card_type) {
		    card_type.preload(audio_player);
		});
	};
    
    cards.ActionCardStack = ActionCardStack;
    cards.RepeatCardStack = RepeatCardStack;
    cards.CardSequence = CardSequence;

    return cards;
});
