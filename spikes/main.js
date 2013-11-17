requirejs.config({
    shim: {
		underscore: {
			exports: '_'
		},
        d3: {
            exports: 'd3'
        },
		when: {
			exports: 'when'
		}
    }
});

require(["robots", "domReady"], function(robots, onDomReady) {
	onDomReady(robots.start);
});
