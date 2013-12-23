define(["d3", "underscore", "react", "robots.cards", "robots.audio", "robots.drag"], function(d3, _, React, cards, audio, drag) {
    'use strict';
	
    var dom = React.DOM;
    var audio_player;
    var program;
	var is_running;
	var card_layout;

	
	
	var CardLayout = React.createClass({
		displayName: "robots.CardLayout",
		
		getInitialState: function() {
			return {program: program};
		},
		render: function() {
			return this.renderSequence(this.state.program);
		},
		renderCard: function(c, extra_attrs) {
			var attrs = {};
			attrs.className = "card " + (c.isAtomic ? "action" : "control"); // React doesn't support classList :-(
			attrs.key = c.id;
			_.extend(attrs, extra_attrs);
			return dom.div(attrs, c.text);
		},
		renderRowElement: function(c) {
			if (c.isAtomic) {
				return this.renderCard(c, {id: c.id});
			}
			else {
				return dom.div({className:"cardgroup", id: c.id},
					this.renderCard(c),
					this.renderSequence(c.body));
			}
		},
		renderRow: function(r) {
			return dom.div({className:"cardrow"},
				_.map(r, this.renderRowElement),
				(r.closed ? [] : [this.renderNewCardDropTarget(r.sequence)]));
		},
		renderSequence: function(s) {
			return dom.div({className:"cardsequence"},
				_.map(s.rows, this.renderRow),
				(s.lastRow().closed ? [dom.div({className:"cardrow"}, this.renderNewCardDropTarget(s))] : []));
		},
		renderNewCardDropTarget: function(sequence) {
			return DropTarget({
				action: "new",
				key: 'append',
				onCardDropped: function(stack) {
					addNewCard(sequence, stack);
				}
			});
		}
	});
	
    var DropTarget = React.createClass({
		displayName: "robots.DropTarget",
		
		render: function() {
			return dom.div({className:"cursor"});
		},
        componentDidMount: function() {
			var n = this.getDOMNode();
			n.addEventListener("carddragin", this.cardDragIn);
			n.addEventListener("carddrop", this.cardDrop);
		},
		cardDragIn: function(ev) {
			drag.accept(ev, drag.action(ev) == this.props.action);
		},
		cardDrop: function(ev) {
			this.props.onCardDropped(drag.data(ev));
		}
	});

    

	function cardText(card) {
		return card.text;
	}
	
	function cardId(card) {
		return card.id;
	}
	
    function viewToRunMode() {
		d3.select("body")
			.classed("editing", false)
			.classed("running", true);
	}
    
    function viewToEditMode() {
		d3.select("body")
			.classed("editing", true)
			.classed("running", false);
		d3.selectAll("#program .active")
			.classed("active",false);
	}
    
    function activateCard(card_name) {
		d3.select("#"+card_name)
			.classed("active", true)
			.each(function() {this.scrollIntoView(false);});
	}
	
	function deactivateCard(card_name) {
		d3.select("#"+card_name).classed("active", false);
	}
    
    function playAudioClip(clip_name, done_callback) {
		audio_player.play(clip_name, done_callback);
	}
	
    function runProgram() {
		is_running = true;
		viewToRunMode();
		program.run({activate: activateCard,
					 deactivate: deactivateCard,
					 play: playAudioClip},
					viewToEditMode);
	}
    
    function stopProgram() {
		console.log("stop");
		is_running = false;
		audio_player.stop();
		viewToEditMode();
	}
	
	function addNewCard(sequence, stack) {
		var card = stack.newCard();
		sequence.append(card);
		
		var cardCount =	program.totalCardCount();
		d3.select("#card-count").text(cardCount);
		
		card_layout.setState(program);
		
		d3.select("#run").attr("enabled", cardCount > 0);
		
		return card;
	}
	
	function start() {
		audio_player = new audio.PausingAudioPlayer(250);
		program = cards.newProgram();
		is_running = false;
		card_layout = CardLayout({});
		
		cards.preload(audio_player);
		
        var new_card_gesture = drag.gesture("new");
		
		d3.select("div").on("touchmove", function() {
		    d3.event.preventDefault();
		});
		
		d3.select("#run").on("click", runProgram);
		d3.select("#stop").on("click", stopProgram);
		
		d3.selectAll("#stacks #control").selectAll(".card")
			.data(_.values(cards.control))
			.enter()
		    .append("div")
		    .attr("draggable", true)
			.classed("card", true)
			.classed("control", true)
			.text(cardText)
		    .call(new_card_gesture);
		
		d3.selectAll("#stacks #actions").selectAll(".card")
			.data(_.values(cards.actions))
			.enter()
		    .append("div")
		    .attr("draggable", true)
			.classed("card", true)
			.classed("action", true)
			.text(cardText)
		    .call(new_card_gesture);
		
		d3.select("body").classed("loading", false);
		
		React.renderComponent(card_layout, document.getElementById("program"));
		
		viewToEditMode();
	}
	
    return {
		start: start
	};
});
