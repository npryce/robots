define(["d3", "underscore", "robots.actions"], function(d3, _, actions) {
    
	function keyboardCardClicked(datum, index) {
		console.log("input card clicked: " + this.getAttribute("data-action"));
	}
	
	function start() {
		d3.selectAll("#keyboard .card").on("click", keyboardCardClicked);
	}
    
    return {
		start: start
	};
});
