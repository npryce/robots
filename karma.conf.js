module.exports = function(config) {
  config.set({
    basePath:'./',
    frameworks: ['mocha','requirejs'],
    files: [
      {pattern: 'test/test-main.js',         included: true,  watched: true,  served: true},
      {pattern: 'test/**/*.js',              included: false, watched: true,  served: true},
      {pattern: 'src/**/*.js',               included: false, watched: true,  served: true},
	  {pattern: 'node_modules/chai/**/*.js', included: false, watched: true,  served: true}
    ],
    reporters:['dots'],
    browsers:['PhantomJS'],
    autoWatch: true,
	singleRun: false,
    colors: true,
    logLevel: config.LOG_INFO
  });
};
