define(["d3", "underscore", "robots.cards", "robots.audio"], function(d3, _, card_types, AudioPlayer) {
    var audio_player = new AudioPlayer();
    var program = [];
	var is_running = false;
    
    function attr(name) {
		return function(obj) {
			return obj[name];
		};
	}
    
    card_types.all().forEach(function (card_type) {
        if (card_type.action) {
			audio_player.load("actions/"+card_type.action);
		}
    });
    
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
	
	function keyboardCardClicked(card_type) {
		program.push(card_type);
		
		var cardCount =	program.length;
		var programPanel = d3.select("#program");
		
		d3.select("#card-count").text(cardCount);
		
		d3.select("#program").selectAll(".card").data(program).enter()
		    .insert("button", "#cursor")
			.attr("type", "button")
			.classed("card", true).classed("action", true)
			.text(attr("text"));
		
		d3.select("#run").attr("enabled", cardCount > 0);
	}
	
	function start() {
		d3.select("#run").on("click", runProgram);
		d3.select("#stop").on("click", stopProgram);
		
		d3.selectAll("#keyboard #actions").selectAll(".card")
			.data(_.values(card_types.actions))
			.enter()
		    .append("button")
			.attr("type", "button")
			.classed("card", true).classed("action", true)
			.text(attr("text"))
			.on("click", keyboardCardClicked);
		
		viewToEditMode();
	}
	
    return {
		start: start
	};
});
