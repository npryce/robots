define(["zepto", "lodash", "react", "robots.cards", "robots.audio", "robots.edit", "robots.gui"], 
	function($, _, React, cards, audio, edit, gui)
{
    'use strict';
	
    var audio_player;
	var history;
	var is_running;
	var card_layout;
	var audio_clip;
	var next_step_callback = _.noop();
	
	function enable(id, flag) {
		document.getElementById(id).disabled = !flag;
	}
	
    function viewToRunMode() {
		$("body").removeClass("editing").addClass("running");
	}
    
    function viewToEditMode() {
		$("body").addClass("editing").removeClass("running");
		$("#program .active").removeClass("active");
		$(".annotation").remove();
		
		enable("again", false);
		enable("next", false);
	}
	
	function cardElement(card_name) {
		return $("#"+card_name);
	}
	
    function activateCard(card_name) {
		cardElement(card_name)
			.addClass("active")
			.each(function() {this.scrollIntoView(false);});
	}
	
	function deactivateCard(card_name) {
		cardElement(card_name)
			.removeClass("active")
			.find(".annotation").remove();
	}
	
	function annotateCard(card_name, annotation) {
		var card = cardElement(card_name);
		card.find(".annotation").remove();
		card.append("<div class='annotation'>" + annotation + "</div>");
	}
    
    function playAudioClip(clip_name, done_callback) {
		audio_clip = clip_name;
		next_step_callback = done_callback;
		audio_player.play(clip_name, audioClipFinished);
	}
	
	function audioClipFinished() {
		enable("again", true);
		enable("next", true);
	}
	
	function playAudioClipAgain() {
		enable("again", false);
		enable("next", false);
		audio_player.play(audio_clip, audioClipFinished);		
	}
	
	function nextStep() {
		enable("again", false);
		enable("next", false);
		next_step_callback();
	}
	
    function runProgram() {
		is_running = true;
		viewToRunMode();
		cards.run(history.current(),
				  {activate: activateCard,
				   deactivate: deactivateCard,
				   annotate: annotateCard,
				   play: playAudioClip},
				  viewToEditMode);
	}
    
    function stopProgram() {
		console.log("stop");
		is_running = false;
		next_step_callback = _.noop();
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
		
		document.getElementById("card-count").innerText = cardCount;
		enable("undo", history.canUndo());
		enable("redo", history.canRedo());
		enable("run", cardCount > 0);
		enable("clear", cardCount > 0);
	}
	
	function undo() {
		updateHistory(history.undo());
	}
	
	function redo() {
		updateHistory(history.redo());
	}
	
	function start() {
		audio_player = new audio.AudioPlayer();
		cards.preload(audio_player);
		
		card_layout = gui.CardLayout({onEdit: onEdit});
		
		$("#clear").on("click", clearProgram);
		$("#undo").on("click", undo);
		$("#redo").on("click", redo);
		$("#run").on("click", runProgram);
		$("#stop").on("click", stopProgram);
		$("#again").on("click", playAudioClipAgain);
		$("#next").on("click", nextStep);
		
		React.renderComponent(card_layout, document.getElementById("program"));
		React.renderComponent(gui.CardStacks({cards: cards}), document.getElementById("stacks"));
		
		updateHistory(edit.undoStartingWith(cards.newProgram()));
		
		$("body").removeClass("loading");
		
		is_running = false;
		viewToEditMode();
	}
	
    return {
		start: start
	};
});
