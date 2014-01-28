define(["lodash", "howler"], function(_, howler) {
    'use strict';
    
    function AudioPlayer() {
		this.formats = ["wav"];
		this.samples = {};
		this.current = null;
		this.completion_callback = _.noop;
		this.is_playing = false;
    }
	
	AudioPlayer.prototype.load = function(sample_name) {
		var sample = new howler.Howl({
			urls: _.map(this.formats, function(f) {return "audio/" + sample_name + "." + f;}),
			buffer: true,
			onend: _.bind(this._sampleFinished, this)
		});
		this.samples[sample_name] = sample;
		sample.load();
	};
    AudioPlayer.prototype.isPlaying = function() {
		return this.is_playing;
	};
    AudioPlayer.prototype.play = function(sample_name, completion_callback) {
		this.current = this.samples[sample_name];
		this.replay(completion_callback);
	};
	AudioPlayer.prototype.replay = function(completion_callback) {
		this.completion_callback = completion_callback;
		this.is_playing = true;
		console.log("playing " + this.current.urls()[0]);
		this.current.play();
	};
	AudioPlayer.prototype._sampleFinished = function() {
		var completion_callback = this.completion_callback;
		this._clearPlayback();
		completion_callback();
	};
	AudioPlayer.prototype.stop = function() {
		if (this.isPlaying()) {
			this.current.stop();
			this._clearPlayback();
		}
	};
	AudioPlayer.prototype._clearPlayback = function() {
		this.completion_callback = _.noop;
		this.is_playing = false;
	};
	
    
	return {
		AudioPlayer: AudioPlayer
	};
});
