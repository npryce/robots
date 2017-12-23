package robots.ui

import browser.EventHandler
import browser.SpeechSynthesisEvent
import browser.SpeechSynthesisUtterance
import browser.SpeechSynthesisVoice
import browser.speechSynthesis

class BrowserSpeech(private val onChange: () -> Unit) : Speech {
    private var selectedVoice: SpeechSynthesisVoice? = null
    var voice: SpeechSynthesisVoice
        get() = selectedVoice ?: defaultVoice()
        set(value) {
            selectedVoice = value
            onChange()
        }
    
    fun voices() =
        speechSynthesis.getVoices().toList()
    
    override val isSpeaking: Boolean
        get() =
            speechSynthesis.speaking
    
    override fun speak(text: String, onSpoken: () -> Unit) {
        val endHandler: EventHandler<SpeechSynthesisEvent> = {
            onChange()
            onSpoken()
        }
        
        speechSynthesis.speak(SpeechSynthesisUtterance(text).apply {
            voice = this@BrowserSpeech.voice
            onstart = { onChange() }
            onend = endHandler
            onerror = endHandler
        })
    }
    
    private fun defaultVoice() =
        speechSynthesis.getVoices().let { it.firstOrNull { it.default } ?: it.first() }
}