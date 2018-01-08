package robots.ui

interface Speech {
    val isSpeaking: Boolean
    fun speak(text: String, onSpoken: () -> Unit = {})
}

fun Speech.speak(text: String?, onSpoken: () -> Unit = {}) {
    if (text == null) {
        onSpoken()
    } else {
        speak(text, onSpoken)
    }
}
