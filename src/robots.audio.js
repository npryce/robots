define(["underscore"], function(_) {
    function AudioPlayer() {
		this.format = "wav";
		this.samples = {};
		this.is_playing = false;
		this.current = new Audio();
		this.current.pause();
		this.timeout = null;
    }
	
	function load(sample_name) {
		var resource_name = "audio/" + sample_name + "." + this.format;
		var sample = new Audio(resource_name);
		this.samples[sample_name] = sample;
	}
	
    function isPlaying() {
		return this.timeout != null;
	}
	
    function play(sample_name, completion_callback) {
		function ended() {
			this.timeout = null;
			completion_callback();
		}
		
        var sample = this.samples[sample_name];
		
		console.log("playing " + sample.src);
		
		this.current.src = sample.src;
		this.current.play();
		this.timeout = setTimeout(_.bind(ended,this), sample.duration*1000);
	}
	
	function stop() {
		if (this.isPlaying()) {
			this.current.pause();
			clearTimeout(this.timeout);
		}
	}
	   
	AudioPlayer.prototype.load = load;
	AudioPlayer.prototype.play = play;
	AudioPlayer.prototype.isPlaying = isPlaying;
    AudioPlayer.prototype.stop = stop;
		
	return AudioPlayer;
});
