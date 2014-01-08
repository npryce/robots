
requirejs.config({
    // Karma serves files from '/base'
    baseUrl: '/base/',
	
	paths: {
		'd3': 'src/d3-3.3.9',
		'lodash': 'src/lodash-2.4.1',
		'robots.cards': 'src/robots.cards',
		'robots.cards2': 'src/robots.cards2',
		'robots.undo': 'src/robots.undo',
		'chai': 'node_modules/chai/chai',
		'fake-context': 'test/fake-context',
		'js-tree-cursor': 'src/js-tree-cursor'
	},
	
    shim: {
        d3: {
            exports: 'd3'
        },
		chai: {
			exports: 'chai'
		},
		'js-tree-cursor': {
			exports: 'TreeCursor'
		}
    },
    
    // ask Require.js to load these files (all our tests)
    deps: Object.keys(window.__karma__.files).filter(function (file) {
        return  /^\/base\/test\/.*-spec\.js$/.test(file);
    }),
	
    // start test run, once Require.js is done
    callback: window.__karma__.start
});
