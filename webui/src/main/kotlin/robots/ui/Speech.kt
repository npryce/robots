package robots.ui

interface Speech {
    val isSpeaking: Boolean
    fun speak(text: String, onSpoken: () -> Unit = {})
}

