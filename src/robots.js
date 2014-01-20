define(["zepto", "lodash", "react", "robots.cards", "robots.audio", "robots.edit", "robots.gui"], 
	function($, _, React, cards, audio, edit, gui)
{
    'use strict';
	
    var audio_player;
	var history;
	var is_running;
	var card_layout;


    function viewToRunMode() {
		$("body").removeClass("editing").addClass("running");
	}
    
    function viewToEditMode() {
		$("body").addClass("editing").removeClass("running");
		$("#program .active").removeClass("active");
	}
	
    function activateCard(card_name) {
		$("#"+card_name)
			.addClass("active")
			.each(function() {this.scrollIntoView(false);});
	}
	
	function deactivateCard(card_name) {
		$("#"+card_name).removeClass("active");
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
		
		$("#clear").on("click", clearProgram);
		$("#run").on("click", runProgram);
		$("#stop").on("click", stopProgram);
		$("#undo").on("click", undo);
		$("#redo").on("click", redo);
		
		React.renderComponent(card_layout, document.getElementById("program"));
		React.renderComponent(gui.CardStacks({cards: cards}), document.getElementById("stacks"));
		
		updateHistory(edit.undoStartingWith(initial_program));
		
		$("body").removeClass("loading");
		
		is_running = false;
		viewToEditMode();
	}
	
    return {
		start: start
	};
});
