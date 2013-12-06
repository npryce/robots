
requirejs.config({
    // Karma serves files from '/base'
    baseUrl: '/base/',
	
	paths: {
		'underscore': '/base/src/underscore',
		'robots.cards': '/base/src/robots.cards',
		'chai': '/base/node_modules/chai/lib/chai'
	},
	
    shim: {
		underscore: {
			exports: '_'
		},
        d3: {
            exports: 'd3'
        }
    },
    
    // ask Require.js to load these files (all our tests)
    deps: Object.keys(window.__karma__.files).filter(function (file) {
        return  /^\/base\/test\/.*-spec\.js$/.test(file);
    }),
	
    // start test run, once Require.js is done
    callback: window.__karma__.start
});
