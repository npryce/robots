package robots.ui

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import robots.Action
import robots.Repeat
import robots.Seq
import robots.editPoints
import robots.splitAfter
import robots.ui.ProgramEditor.Props


class ProgramEditor(props: Props) : RComponent<Props, RState>(props) {
    interface Props : RProps {
        var program: Seq
    }
    
    override fun RBuilder.render() {
        props.program.editPoints()
            .splitAfter { it.node is Repeat }
            .forEach { row ->
                div("cardsequence") {
                    row.forEach { editPoint ->
                        if (editPoint.node is Action) {
                            card(editPoint)
                        } else {
                            // Hack job to get something interesting on screen
                            div("cardgroup") {
                                card(editPoint)
                                (editPoint.node as Repeat).repeated.editPoints { TODO() }.forEach { card(it) }
                            }
                        }
                    }
                }
            }
    }
}

fun RBuilder.programEditor(edited: Seq) = child(ProgramEditor::class) {
    attrs.program = edited
}
