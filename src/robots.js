define(["d3", "underscore", "robots.cards", "robots.audio", "robots.drag"], function(d3, _, cards, audio, drag) {
    'use strict';
    
    var audio_player = new audio.PausingAudioPlayer(250);
    var program = cards.newProgram();
	var is_running = false;
    var dragged_card = null;
    
    cards.preload(audio_player);
	
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
	
	function addNewCard(stack) {
		var card = stack.newCard();
		program.append(card);
		
		var cardCount =	program.totalCardCount();
		d3.select("#card-count").text(cardCount);
		
		bindProgramToHtml();
		
		d3.select("#run").attr("enabled", cardCount > 0);
		
		return card;
	}
	
	function bindProgramToHtml() {
		bindSequenceToHtml(d3.select("#program"), program);
	}
	
    function bindSequenceToHtml(container, card_sequence) {
		var cards = container.selectAll(".card").data(card_sequence.toArray(), cardId);
		
		cards.enter().insert("div", ".cursor")
			.attr("id", cardId)
			.classed("card", true)
			.classed("action", function(card) { return card.isAtomic; })
			.classed("control", function(card) { return !card.isAtomic; })
			.text(cardText);
		
		cards.exit().remove();
		
		if (card_sequence.isEmpty()) {
			container.append("div")
				.classed("cursor", "true")
				.on("carddrag", function() {d3.event.detail.handler = addNewCard;});
		}
		else {
			//container.select(".cursor").remove();
		}
	}
	
	function start() {
        var new_card_gesture = drag.newCard(addNewCard);
		
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
		
		bindProgramToHtml();
		viewToEditMode();
	}
	
    return {
		start: start
	};
});
