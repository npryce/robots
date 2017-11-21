package robots.ui

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import robots.Seq
import robots.editPoints
import robots.ui.ProgramEditor.Props

class ProgramEditor(props: Props) : RComponent<Props, RState>(props) {
    interface Props : RProps {
        var program: Seq
    }
    
    override fun RBuilder.render() {
        props.program.editPoints()
            .forEach { card(it) }
    }
}

fun RBuilder.programEditor(edited: Seq) = child(ProgramEditor::class) {
    attrs.program = edited
}