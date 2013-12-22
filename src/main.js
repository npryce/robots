requirejs.config({
    paths: {
		'react': 'react-0.8.0',
		'underscore': 'underscore-1.5.2'
	},
    shim: {
		'underscore': {
			exports: '_'
		},
        'd3': {
            exports: 'd3'
        }
    }
});

require(["robots", "domReady"], function(robots, onDomReady) {
	onDomReady(robots.start);
});
