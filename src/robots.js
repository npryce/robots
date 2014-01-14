define(["d3", "lodash", "react", "robots.cards2", "robots.audio", "robots.edit", "robots.gui"], 
	function(d3, _, React, cards, audio, edit, gui)
{
    'use strict';
	
    var audio_player;
	var history;
	var is_running;
	var card_layout;
	


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

	function clearProgram() {
		onEdit([]);
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
		cards.run(history.current(),
				  {activate: activateCard,
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
	
	function onEdit(new_program) {
		history = history.push(new_program);
		card_layout.programChanged(new_program);
		
		var cardCount =	cards.programSize(new_program);
		d3.select("#card-count").text(cardCount);
		d3.select("#run").attr("enabled", cardCount > 0);
	}
	
	function start() {
		audio_player = new audio.PausingAudioPlayer(250);
		cards.preload(audio_player);
		
		history = edit.undoStartingWith(cards.newProgram());
		is_running = false;
		
		card_layout = gui.CardLayout({program: history.current(), onEdit: onEdit});
		
		d3.select("#clear").on("click", clearProgram);
		d3.select("#run").on("click", runProgram);
		d3.select("#stop").on("click", stopProgram);
		
		React.renderComponent(card_layout, document.getElementById("program"));
		React.renderComponent(gui.CardStacks({cards: cards}), document.getElementById("stacks"));
		
		d3.select("body").classed("loading", false);
		
		viewToEditMode();
	}
	
    return {
		start: start
	};
});
