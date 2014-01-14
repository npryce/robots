define(["lodash", "howler"], function(_, howler) {
    'use strict';
    
    function AudioPlayer() {
		this.formats = ["wav"];
		this.samples = {};
		this.current = null;
		this.completion_callback = _.noop;
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
		return this.current != null;
	};
    AudioPlayer.prototype.play = function(sample_name, completion_callback) {
		this.completion_callback = completion_callback;
		this.current = this.samples[sample_name];
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
		this.current = null;
		this.completion_callback = _.noop;
	};
	
	function PausingAudioPlayer(pause_between_clips, timer, audio_player) {
		this.timer = timer || window;
		this.audio_player = audio_player || new AudioPlayer(this.timer);
		this.pause_between_clips = pause_between_clips;
		this.is_first_clip = true;
		this.timeout = null;
	}
	PausingAudioPlayer.prototype.load = function(clip) {
		this.audio_player.load(clip);
	};
    PausingAudioPlayer.prototype.play = function(sample_name, completion_callback) {
		var playSample = _.bind(function() {
			this.audio_player.play(sample_name, completion_callback);
			this.timeout = null;
		}, this);
		
		if (this.is_first_clip) {
			this.is_first_clip = false;
			playSample();
		}
		else {
			this.timeout = this.timer.setTimeout(playSample, this.pause_between_clips);
		}
	};
    PausingAudioPlayer.prototype.stop = function(sample_name, completion_callback) {
		if (this.timeout != null) {
			this.timer.clearTimeout(this.timeout);
			this.timeout = null;
		} else {
			this.audio_player.stop();
		}
		
		this.is_first_clip = true;
	};
    
	return {
		AudioPlayer: AudioPlayer,
		PausingAudioPlayer: PausingAudioPlayer
	};
});
