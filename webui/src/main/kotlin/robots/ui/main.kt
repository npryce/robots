package robots.ui

import dnd.DragAndDrop
import react.dom.render
import kotlin.browser.document


fun main(args: Array<String>) {
    DragAndDrop.activate()
    
    val containerDiv = document.getElementById("app") ?: throw IllegalStateException("no element called 'app")
    render(containerDiv) {
        app()
    }
}

