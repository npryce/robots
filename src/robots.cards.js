define(["lodash"], function(_){
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
		context.performAction(card.action, card.title, onfinished);
	}
	
	function evalRepeat(card, context, onfinished) {
        var iteration = 0;
		
        function runNextIteration() {
		    if (iteration < card.repeat) {
				context.annotate(card.id, card.repeat - iteration);
				iteration++;
				evalSequence(card.body, context, runNextIteration);
			} else {
				onfinished();
			}
		}
		
		runNextIteration();
	}
	
	function newProgram() {
		return [];
	}

	function newCard(card_type, card_id) {
		var card = _.create(card_type);
		card.id = card_id;
		_.each(card.branches, function(b) {card[b] = [];});
		return card;
	}
	
	function setPropertyOf(card, property_name, new_value) {
		return _.defaults(_.create(Object.getPrototypeOf(card), _.zipObject([[property_name, new_value]])), card);
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
	
	function isControlCard(c) {
		return !isAtomicCard(c);
	}
	
	function isAtomicCard(c) {
		return _.isEmpty(c.branches);
	}
	

	var cards = {
		newProgram: newProgram,
		newCard: newCard,
		setPropertyOf: setPropertyOf,
		programSize: programSize,
		cardSize: cardSize,
		isAtomicCard: isAtomicCard,
		isControlCard: isControlCard,
		run: evalSequence,
		
		action: [
			{
				action: "step-forward",
				title: "Step Forward",
				eval: evalAction
			},
			{
				action: "step-backward",
				title: "Step Backward",
				eval: evalAction
			},
			{
				action: "turn-anticlockwise",
				title: "Turn Anticlockwise",
				eval: evalAction
			},
			{
				action: "turn-clockwise",
				title: "Turn Clockwise",
				eval: evalAction
			},
			{
				action: "pick-up",
				title: "Pick Up",
				eval: evalAction
			},
			{
				action: "put-down",
				title: "Put Down",
				eval: evalAction
			},
			{
				action: "shoot",
				title: "Shoot",
				eval: evalAction
			}
		],
		repeat: _.map(_.range(2,11), function(i) {
			return {
				repeat: i,
				branches: ["body"],
				title: "Repeat " + i + " " + pluralise("Time", i),
				text: String(i) + "×",
				eval: evalRepeat
			};
		}),
		
		eval: {
			action: evalAction,
			repeat: evalRepeat
		}
	};
	
	return cards;
});
