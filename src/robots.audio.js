define(["underscore"], function(_) {
    'use strict';
    
    function AudioPlayer(timer) {
		this.timer = timer || window;
		this.format = "wav";
		this.samples = {};
		this.is_playing = false;
		this.current = new Audio();
		this.current.pause();
		this.timeout = null;
    }
	
	AudioPlayer.prototype.load = function(sample_name) {
		var resource_name = "audio/" + sample_name + "." + this.format;
		var sample = new Audio(resource_name);
		this.samples[sample_name] = sample;
	};
    AudioPlayer.prototype.isPlaying = function() {
		return this.timeout != null;
	};
    AudioPlayer.prototype.play = function(sample_name, completion_callback) {
		function ended() {
			this.timeout = null;
			completion_callback();
		}
		
        var sample = this.samples[sample_name];
		
		console.log("playing " + sample.src);
		
		this.current.src = sample.src;
		this.current.play();
		this.timeout = this.timer.setTimeout(_.bind(ended,this), sample.duration*1000);
	};
	AudioPlayer.prototype.stop = function() {
		if (this.isPlaying()) {
			this.current.pause();
			this.timer.clearTimeout(this.timeout);
			this.timeout = null;
		}
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
