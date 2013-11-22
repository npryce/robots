define(["d3", "underscore", "robots.cards", "robots.audio"], function(d3, _, cards, AudioPlayer) {
    var audio_player = new AudioPlayer();
    var program = [];
	var is_running = false;
    var dragged_card = null;
    
    function attr(name) {
		return function(obj) {
			return obj[name];
		};
	}
    
    cards.preload(audio_player);
    
    function viewToRunMode() {
		d3.select("body")
			.classed("editing", false)
			.classed("running", true);
	}
    
    function viewToEditMode() {
		d3.select("body")
			.classed("editing", true)
			.classed("running", false);
	}
    
    function runProgram() {
		var step = 0;
		
		function pauseBetweenInstructions() {
			console.log("pausing between instructions");
			setTimeout(runNextProgramStep, 250);
		}
		
		function runNextProgramStep() {
			if (is_running) {
				if (step < program.length) {
					audio_player.play("actions/" + program[step].action, pauseBetweenInstructions);
					step++;
				}
				else {
					viewToEditMode();			
				}
			}
		}
		
		is_running = true;
		viewToRunMode();
		runNextProgramStep();
	}
    
    function stopProgram() {
		console.log("stop");
		is_running = false;
		audio_player.stop();
		viewToEditMode();
	}
	
    function toggleProgram() {
		if (is_running) {
			stopProgram();
		} else {
			runProgram();
		}
	}
    
	function addNewCard(card_type) {
		program.push(card_type);
		
		var cardCount =	program.length;
		var programPanel = d3.select("#program");
		
		d3.select("#card-count").text(cardCount);
		
		d3.select("#program").selectAll(".card").data(program).enter()
		    .insert("div", "#cursor")
			.classed("card", true).classed("action", true)
			.text(attr("text"));
		
		d3.select("#run").attr("enabled", cardCount > 0);
	}
	
    function newCardDragStarted(card_type) {
		var dt = d3.event.dataTransfer;
		dt.effectAllowed = 'copy';
		dt.setData("application/x-robot-card", "");
		
		dragged_card = card_type;
	}

    function newCardDragEnded(card_type) {
		dragged_card = null;
	}
    
    function newCardDragEnter() {
		d3.event.preventDefault();
		return true;
	}
	
    function newCardDragOver() {
		d3.event.preventDefault();
		return true;
	}
    
    function newCardDropped() {
		addNewCard(dragged_card);
		d3.event.stopPropagation();
	}
	
	function start() {
		d3.select("div").on("touchmove", function() {
		    d3.event.preventDefault();
		});
		
		d3.select("#run").on("click", runProgram);
		d3.select("#stop").on("click", stopProgram);
		
		d3.selectAll("#keyboard #actions").selectAll(".card")
			.data(_.values(cards.actions))
			.enter()
		    .append("div")
		    .attr("draggable", true)
			.classed("card", true)
			.classed("action", true)
			.text(attr("text"))
		    .on("dragstart", newCardDragStarted)
		    .on("dragend", newCardDragEnded);
		
		d3.select("#cursor")
		    .on("dragenter", newCardDragEnter)
		    .on("dragover", newCardDragOver)
			.on("drop", newCardDropped);
		
		d3.select("body").classed("loading", false);
		viewToEditMode();
	}
	
    return {
		start: start
	};
});
