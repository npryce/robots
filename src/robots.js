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
	
	function addNewCard(sequence, stack) {
		var card = stack.newCard();
		sequence.append(card);
		
		var cardCount =	program.totalCardCount();
		d3.select("#card-count").text(cardCount);
		
		bindProgramToHtml();
		
		d3.select("#run").attr("enabled", cardCount > 0);
		
		return card;
	}
	
	function bindCardToHtml(step_div, card) {
		step_div
			.attr("id", cardId)
			.classed("step", true);
		
		if (card.isAtomic) {
			step_div
				.classed("card", true)
				.classed("action",  true)
				.text(cardText);
		} else {
			step_div
				.classed("cardgroup", true);
			
			step_div.append("div")
				.classed("card", true)
				.classed("control", true)
				.text(cardText);
			
			var body_selection = step_div.append("div")
				.classed("bodycards", true);
			
			bindSequenceToHtml(body_selection, card.body);
		}
	}

    function updateRowHtml(row) {
		var steps = row_sel.selectAll(".step").data(row, cardId);
		
		// FUTURE - will have to be changed when the app supports removing cards
		
		if (row.closed) {
			row_sel.select(".cursor").remove();
		} else if (row_sel.select(".cursor").size() == 0) {
			row_sel
				.append("div")
				.classed("cursor", "true")
				.on("carddragin", function() {
						drag.accept(d3.event, drag.action(d3.event) == "new");
					})
				.on("carddrop", function() {
						addNewCard(sequence, drag.data(d3.event));
					});
		}
		
		steps.enter()
			.insert("div",".cursor")
			.each(function(card) { 
					  bindCardToHtml(d3.select(this), card); 
				  });
		steps.exit().remove();
	}
	
	function updateSequenceHtml(sequence) {
		var rows = container.selectAll(".row").data(sequence.rows());
		rows.enter()
			.append("div").classed("row", true);
		rows.each(updateRowHtml);
		rows.exit().remove();
	}
	
	function bindProgramToHtml() {
		updateSequenceHtml.call(document.getElementById("program"), program);
	}
	
	function start() {
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
		
		bindProgramToHtml();
		viewToEditMode();
	}
	
    return {
		start: start,
		internal: {
			toRows: toRows
		}
	};
});
