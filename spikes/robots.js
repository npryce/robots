define(["d3", "underscore", "robots.cards", "robots.audio"], function(d3, _, cards, AudioPlayer) {
    var audio_player = new AudioPlayer();
    
    _(cards).values().forEach(function (card) {
        if (card.action) {
			audio_player.load(card.action);
		}
    });
	
	function keyboardCardClicked(datum, index) {
		var action = this.getAttribute("data-action");
		audio_player.play(action);
	}
	
	function start() {
		d3.selectAll("#keyboard .card").on("click", keyboardCardClicked);
	}
    
    return {
		start: start
	};
});
