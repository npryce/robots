define(["modash", "js-tree-cursor"], function(_, treecursor){
	'use strict';
	
	function evalCard(card, context, onfinished) {
		context.activate(card.id);
		card.eval(card, context, function() {
			context.deactivate(card.id);
			onfinished();
		});
	}
	
	function evalSequence(sequence, context, onfinished) {
		var nextStep = 0;
		
		function runNextStep() {
			if (nextStep < sequence.length) {
				var step = sequence[nextStep];
				nextStep++;
				evalCard(step, context, runNextStep);
			}
			else {
				onfinished();
			}
		};
		
		runNextStep();
	}
	
	function evalAction(card, context, onfinished) {
		context.play("actions/" + card.action, onfinished);
	}
	
	function evalRepeat(card, context, onfinished) {
        var iteration = 0;
		
        function runNextIteration() {
		    if (iteration < card.repeat) {
				iteration++;
				evalSequence(card.body, context, runNextIteration);
			} else {
				onfinished();
			}
		}
		
		runNextIteration();
	}
	
	function newCard(card_type) {
		var card = _.create(card_type);
		card.id = _.uniqueId("card");
		_.each(card.branches, function(b) {card[b] = [];});
		return card;
	}

	function newProgram() {
		return [];
	}

	function sum(a, b) {
		return a + b;
	}
	
	function cardSize(card) {
		return 1 + _(card).at(card.branches).flatten().map(programSize).reduce(sum, 0);
	}
	
	function programSize(seq) {
		return _(seq).map(cardSize).reduce(sum, 0);
	}
	
	function pluralise(s, i) {
		return (i > 1) ? s + "s" : s;
	}
	
	function cursorOpen(x) {
		if (_.isArray(x)) {
			return x;
		}
		else {
			return _.at(x, x.branches);
		}
	}
	
	function cursorClose(x, children) {
		if (_.isArray(x)) {
			return children;
		}
		else {
			return _.defaults(_.create(Object.getPrototypeOf(x)), _.zipObject(x.branches, children), x);
		}
	}
	
	function isControlCard(c) {
		return !isAtomicCard(c);
	}

	function isAtomicCard(c) {
		return _.isEmpty(c.branches);
	}
	
	function isAtomic(x) {
		return !_.isArray(x) && isAtomicCard(x);
	}
	
	function cursor(x) {
        return TreeCursor.adaptTreeCursor(x, cursorOpen, cursorClose, isAtomic);
	}
	
	var cards = {
		newProgram: newProgram,
		newCard: newCard,
		programSize: programSize,
		cursor: cursor,
		isAtomicCard: isAtomicCard,
		
		action: [
			{
				action: "step-forward",
				title: "Step Forward",
				text: "\u21E7",
				eval: evalAction
			},
			{
				action: "step-backward",
				title: "Step Backward",
				text: "\u21E9",
				eval: evalAction
			},
			{
				action: "turn-clockwise",
				title: "Turn Clockwise",
				text: "\u21B7",
				eval: evalAction
			},
			{
				action: "turn-clockwise",
				title: "Turn Anticlockwise",
				text: "\u21B6",
				eval: evalAction
			},
			{
				action: "jump-forward",
				title: "Jump Forward",
				text: "\u21EA",
				eval: evalAction
			},
			{
				action: "pick-up",
				title: "Pick Up",
				text: "\u261D",
				eval: evalAction
			},
			{
				action: "put-down",
				title: "Put Down",
				text: "\u261F",
				eval: evalAction
			}
		],
		repeat: _.map(_.range(2,11), function(i) {
			return {
				repeat: i,
				branches: ["body"],
				title: "Repeat " + i + " " + pluralise("Time", i),
				text: String.valueOf(i),
				eval: evalRepeat
			};
		}),
		
		eval: {
			sequence: evalSequence,
			action: evalAction,
			repeat: evalRepeat
		}
	};
	
	return cards;
});
