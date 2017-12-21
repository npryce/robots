package robots.ui

import browser.EventHandler
import browser.SpeechSynthesisEvent
import browser.SpeechSynthesisUtterance
import browser.SpeechSynthesisVoice
import browser.speechSynthesis

interface Speech {
    val isSpeaking: Boolean
    fun speak(text: String, onSpoken: ()->Unit = {})
}

class BrowserSpeech(private val onChange: () -> Unit) : Speech {
    var voice: SpeechSynthesisVoice? = null // use default
    
    fun voices() =
        speechSynthesis.getVoices().toList()
    
    override val isSpeaking: Boolean
        get() =
            speechSynthesis.speaking
    
    override fun speak(text: String, onSpoken: ()->Unit) {
        val endHandler : EventHandler<SpeechSynthesisEvent> = {
            onSpoken()
            onChange()
        }
        
        speechSynthesis.speak(SpeechSynthesisUtterance(text).apply {
            onstart = endHandler
            onend = endHandler
            onerror = endHandler
        })
    }
}
