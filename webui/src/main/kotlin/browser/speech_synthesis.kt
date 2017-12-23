package browser

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

typealias EventHandler<E> = (E) -> Unit

external val speechSynthesis: SpeechSynthesis

external class SpeechSynthesis : EventTarget {
    val paused: Boolean
    val pending: Boolean
    val speaking: Boolean
    
    fun getVoices(): Array<SpeechSynthesisVoice>
    
    fun cancel()
    fun pause()
    fun resume()
    fun speak(utterance: SpeechSynthesisUtterance)
    
    var onvoiceschanged: EventHandler<Event>
}

external interface SpeechSynthesisVoice {
    val voiceURI: String
    val name: String
    val lang: String
    val localService: Boolean
    val default: Boolean
}

external class SpeechSynthesisUtterance(text: String) : EventTarget {
    var text: String
    var lang: String
    var voice: SpeechSynthesisVoice?
    var volume: Double
    var rate: Double
    var pitch: Double
    
    var onstart: EventHandler<SpeechSynthesisEvent>
    var onend: EventHandler<SpeechSynthesisEvent>
    var onerror: EventHandler<SpeechSynthesisErrorEvent>
    var onpause: EventHandler<SpeechSynthesisEvent>
    var onresume: EventHandler<SpeechSynthesisEvent>
    var onmark: EventHandler<SpeechSynthesisEvent>
    var onboundary: EventHandler<SpeechSynthesisEvent>
}

external open class SpeechSynthesisEvent : Event {
    val utterance: SpeechSynthesisUtterance
    val charIndex: Long
    val elapsedTime: Float
    val name: String
}


external class SpeechSynthesisErrorEvent : SpeechSynthesisEvent {
    val error: ErrorCode
}

typealias ErrorCode = String

val canceled = "canceled"
val interrupted = "interrupted"
val audio_busy = "audio-busy"
val audio_hardware = "audio-hardware"
val network = "network"
val synthesis_unavailable = "synthesis-unavailable"
val synthesis_failed = "synthesis-failed"
val language_unavailable = "language-unavailable"
val voice_unavailable = "voice-unavailable"
val text_too_long = "text-too-long"
val invalid_argument = "invalid-argument"
