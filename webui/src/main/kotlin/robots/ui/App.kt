package robots.ui

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.span
import robots.Seq
import robots.UndoRedoStack
import robots.cost
import robots.havingDone


external interface AppProps : RProps {
    var program: Seq
}

external interface AppState : RState {
    var undo: UndoRedoStack<Seq>
}

class App(props: AppProps) : RComponent<AppProps, AppState>(props) {
    init {
        state.undo = UndoRedoStack(props.program)
    }
    
    override fun RBuilder.render() {
        header(state.undo.current)
        programEditor(state.undo.current, onEdit = ::pushUndoRedoState)
        controlPanel()
    }
    
    private fun pushUndoRedoState(newProgram: Seq) {
        setState({ it.apply { undo = undo.havingDone(newProgram) } })
    }
}


fun RBuilder.app(initialProgram: Seq = Seq()) = child(App::class) {
    attrs.program = initialProgram
}

fun RBuilder.header(program: Seq) {
    div("header") {
        +"Cost: "
        span("cost") { +"£${program.cost()}" }
    }
}

private fun RBuilder.controlPanel() {
    div("controls") {
        cardStacks()
    }
}
