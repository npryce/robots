define(["d3", "lodash", "react", "robots.cards", "robots.audio", "robots.edit", "robots.gui"], 
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
		updateHistory(history.push(new_program));
	}
	
	function clearProgram() {
		onEdit([]);
	}
	
	function updateHistory(new_history) {
		history = new_history;
		
		card_layout.programChanged(history.current());
		
		var cardCount =	cards.programSize(history.current());
		
		document.getElementById("card-count").text = cardCount;
		document.getElementById("undo").disabled = !history.canUndo();
		document.getElementById("redo").disabled = !history.canRedo();
		document.getElementById("run").disabled = (cardCount == 0);
		document.getElementById("clear").disabled = (cardCount == 0);
	}
	
	function undo() {
		updateHistory(history.undo());
	}
	
	function redo() {
		updateHistory(history.redo());
	}
	
	function start() {
		audio_player = new audio.PausingAudioPlayer(250);
		cards.preload(audio_player);
		
		var initial_program = cards.newProgram();
		
		card_layout = gui.CardLayout({program: initial_program, onEdit: onEdit});
		
		d3.select("#clear").on("click", clearProgram);
		d3.select("#run").on("click", runProgram);
		d3.select("#stop").on("click", stopProgram);
		d3.select("#undo").on("click", undo);
		d3.select("#redo").on("click", redo);
		
		React.renderComponent(card_layout, document.getElementById("program"));
		React.renderComponent(gui.CardStacks({cards: cards}), document.getElementById("stacks"));
		
		updateHistory(edit.undoStartingWith(initial_program));
		
		d3.select("body").classed("loading", false);
		
		is_running = false;
		viewToEditMode();
	}
	
    return {
		start: start
	};
});
