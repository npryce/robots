package robots.ui

import dnd.DragAndDrop
import react.RBuilder
import react.dom.div
import react.dom.render
import robots.Action
import robots.Repeat
import robots.Seq
import kotlin.browser.document


fun main(args: Array<String>) {
    DragAndDrop.activate()
    
    val containerDiv = document.getElementById("app") ?: throw IllegalStateException("no element called 'app")
    
    val program = Seq(Action("a"), Repeat(3, Action("b"), Action("c"), Repeat(2, Action("x")), Action("y")), Action("d"), Action("e"))
    
    render(containerDiv) {
        programEditor(program)
        controlPanel()
    }
}

private fun RBuilder.controlPanel() {
    div("controls") {
        cardStacks()
    }
}
