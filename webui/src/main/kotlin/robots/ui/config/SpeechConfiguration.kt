package robots.ui.config

import browser.SpeechSynthesisVoice
import kotlinx.html.InputType.text
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSelectFunction
import kotlinx.html.role
import org.w3c.dom.HTMLTextAreaElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button
import react.dom.defaultValue
import react.dom.div
import react.dom.input
import react.dom.set
import react.dom.span
import robots.ui.BrowserSpeech
import robots.ui.Speech
import kotlin.browser.window

private interface SpeechPreviewProps : RProps {
    var speech: Speech
}

private val defaultSampleText = "The quick brown fox jumps over the lazy dog"

private class SpeechPreview(props: SpeechPreviewProps) : RComponent<SpeechPreviewProps, RState>(props) {
    private var sampleTextInput: HTMLTextAreaElement? = null
    
    override fun RBuilder.render() {
        div("speech-preview") {
            span { +"Listen:" }
            input(type = text, classes = "sample-text") {
                ref { sampleTextInput = it.unsafeCast<HTMLTextAreaElement>() }
                attrs.defaultValue = defaultSampleText
                attrs.size = defaultSampleText.length.toString()
            }
            button {
                attrs.disabled = props.speech.isSpeaking
                attrs.onClickFunction = { props.speech.speak(sampleTextInput?.value ?: defaultSampleText) }
                +"▶︎"
            }
        }
    }
}

private fun RBuilder.speechPreview(speech: BrowserSpeech) = child(SpeechPreview::class) {
    attrs.speech = speech
}

fun RBuilder.speechConfiguration(speech: BrowserSpeech, languages: Set<String> = preferredLanguages()) {
    fun SpeechSynthesisVoice.isSelectable() =
        lang.isUnderstoodBySpeakersOf(languages) || default
    
    val currentVoice = speech.voice
    
    configPanel("speech") {
        div("$configItemsClass radiogroup") {
            speech.voices().filter { it.isSelectable() }.forEach { voice ->
                val isSelected = voice === currentVoice
                
                div {
                    key = voice.name
                    attrs.role = "radio"
                    attrs["aria-checked"] = isSelected.toString()
                    attrs.onClickFunction = { speech.voice = voice }
                    attrs.onSelectFunction = { speech.voice = voice }
                    
                    +voice.name
                }
            }
        }
        
        buttonBar {
            speechPreview(speech)
        }
    }
}

private fun preferredLanguages() =
    setOf(*window.navigator.languages) + window.navigator.language

private fun String.isUnderstoodBySpeakersOf(languages: Set<String>) =
    languages.any { it == this || startsWith("$it-") }
