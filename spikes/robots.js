define(["d3", "underscore", "robots.cards", "robots.audio"], function(d3, _, card_types, AudioPlayer) {
    var audio_player = new AudioPlayer();
    var program = [];
	
    function attr(name) {
		return function(obj) {
			return obj[name];
		};
	}
    
    card_types.all().forEach(function (card_type) {
        if (card_type.action) {
			audio_player.load("action-"+card_type.action);
		}
    });
    audio_player.load("click");
    
    function runProgram() {
		console.log("run");
	}
	
	function keyboardCardClicked(card_type) {
		audio_player.play("click");
		program.push(card_type);
		
		var programPanel = d3.select("#program");
		
		d3.select("#program").selectAll(".card").remove().data(program).enter()
		    .append("button")
			.attr("type", "button")
			.classed("card", true).classed("program", true).classed("action", true)
			.text(attr("text"));
		d3.select("#program").append("button")
			.attr("type", "button")
			.classed("card", true).classed("focus", true);
	}
	
	function start() {
		d3.select("#run").on("click", runProgram);
		
		d3.selectAll("#keyboard #actions").selectAll(".card")
			.data(_.values(card_types.actions))
			.enter()
		    .append("button")
			.attr("type", "button")
			.classed("card", true).classed("action", true)
			.text(attr("text"))
			.on("click", keyboardCardClicked);
	}
	
    return {
		start: start
	};
});
