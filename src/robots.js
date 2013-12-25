define(["d3", "lodash", "react", "robots.cards", "robots.audio", "robots.drag", "robots.cardlayout"], 
	function(d3, _, React, cards, audio, drag, layout) 
{
    'use strict';
	
    var audio_player;
    var program;
	var is_running;
	var card_layout;

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
		cards.preload(audio_player);
		
		program = cards.newProgram();
		is_running = false;
		card_layout = layout.CardLayout({program: program, onNewCardDropped: addNewCard});
		
		
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
			.classed("card", true)
			.classed("control", true)
			.text(cardText)
		    .call(new_card_gesture);
		
		d3.selectAll("#stacks #actions").selectAll(".card")
			.data(_.values(cards.actions))
			.enter()
		    .append("div")
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
