package robots.ui

import react.dom.render
import robots.Action
import robots.Repeat
import robots.Seq
import kotlin.browser.document


fun main(args: Array<String>) {
    val app = document.getElementById("app") ?: throw IllegalStateException("no element called 'app")
    
    val program = Seq(Action("a"), Repeat(3, Action("b"), Action("c"), Repeat(2, Action("x")), Action("y")), Action("d"), Action("e"))
    
    render(app) {
        programEditor(program)
    }
}
