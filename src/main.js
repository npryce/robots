requirejs.config({
    paths: {
		'react': 'react-0.8.0',
		'lodash': 'lodash-2.4.1'
	},
    shim: {
        'd3': {
            exports: 'd3'
        }
    }
});

require(["robots", "domReady"], function(robots, onDomReady) {
	onDomReady(robots.start);
});
