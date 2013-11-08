define(["underscore"], function(_) {
    function AudioPlayer() {
		this.format = "wav";
		this.samples = {};
		this.onload = function() {};
		this.loading = 0;
        this.completion_callback = function() {};
    }
	
	function load(sample_name) {
		var resource_name = "audio/" + sample_name + "." + this.format;
		console.log("loading " + resource_name);
		var sample = new Audio(resource_name);
		sample.onload = _.bind(function() { this.sampleLoaded(resource_name); }, this);
		this.samples[sample_name] = sample;
		this.loading += 1;
	}
	
	function sampleLoaded(resource_name) {
		console.log("resource_name loaded");
		this.loading -= 1;
		if (this.loading == 0) {
			var notification = this.onload;
			notification();
		}
	}
    
    function play(card_name, completion_callback) {
        this.completion_callback = completion_callback;
        var sample = this.samples[card_name];
		sample.play();
	}
	
	AudioPlayer.prototype.load = load;
	AudioPlayer.prototype.sampleLoaded = sampleLoaded;
	AudioPlayer.prototype.play = play;
		
	return AudioPlayer;
});
