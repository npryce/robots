package robots.ui

import dnd.DragAndDrop
import react.dom.render
import robots.Action
import robots.Repeat
import robots.Seq
import kotlin.browser.document


fun main(args: Array<String>) {
    DragAndDrop.activate()
    
    val initialProgram = Seq(
        Action("a"),
        Repeat(3,
            Action("b"),
            Action("c"),
            Repeat(2,
                Action("x")),
            Action("y")),
        Action("d"),
        Action("e"))
    
    val containerDiv = document.getElementById("app") ?: throw IllegalStateException("no element called 'app")
    render(containerDiv) {
        app(initialProgram)
    }
}

