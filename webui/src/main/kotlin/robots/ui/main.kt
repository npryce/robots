package robots.ui

import react.dom.render
import robots.Action
import robots.Seq
import kotlin.browser.document


fun main(args: Array<String>) {
    val app = document.getElementById("app") ?: throw IllegalStateException("no element called 'app")
    
    val program = Seq(Action("a"), Action("b"), Action("c"), Action("d"))
    
    render(app) {
        programEditor(program)
    }
}
