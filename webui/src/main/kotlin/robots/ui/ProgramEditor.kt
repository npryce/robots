package robots.ui

import dnd.draggable
import kotlinx.html.DIV
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.RDOMBuilder
import react.dom.div
import robots.Action
import robots.EditPoint
import robots.Repeat
import robots.Seq
import robots.children
import robots.editPoints
import robots.splitAfter
import robots.ui.ProgramEditor.Props


private interface CardProps : RProps {
    var editor: EditPoint
}

private class ExtensionSpace(props: ExtensionSpace.Props) : RComponent<ExtensionSpace.Props, RState>(props) {
    interface Props : CardProps
    
    override fun RBuilder.render() {
        div("cursor") {}
    }
}

fun RBuilder.extensionSpace(editor: EditPoint) = child(ExtensionSpace::class) {
    attrs.editor = editor
}


private class ActionCard(props: CardProps) : RComponent<CardProps, RState>(props) {
    override fun RBuilder.render() {
        draggable(dataProvider = { props.editor.node }) {
            div("card action") { +props.editor.displayId() }
        }
    }
}

fun RBuilder.actionCard(editor: EditPoint) = child(ActionCard::class) {
    attrs.editor = editor
}

private class ControlCard(props: CardProps) : RComponent<CardProps, RState>(props) {
    override fun RBuilder.render() {
        div("card control") { +props.editor.displayId() }
    }
}

fun RBuilder.controlCard(editor: EditPoint) = child(ControlCard::class) {
    attrs.editor = editor
}

fun RDOMBuilder<DIV>.repeatBlock(editPoint: EditPoint) {
    div("cardblock") {
        controlCard(editPoint)
        cardSequence(editPoint.children())
    }
}

private class SequenceEditor(props: Props) : RComponent<SequenceEditor.Props, RState>(props) {
    interface Props : RProps {
        var elements: List<EditPoint>
    }
    
    override fun RBuilder.render() {
        div("cardsequence") {
            props.elements
                .splitAfter { it.node is Repeat }
                .forEach { row -> cardRow(row) }
        }
    }
    
    private fun RDOMBuilder<DIV>.cardRow(row: List<EditPoint>) {
        div("cardrow") {
            row.forEach { editPoint -> cardRowElement(editPoint) }
            
            // TODO - handle empty lists with a mandatory "add first element" cursor
            row.lastOrNull()?.let {
                if (it.node !is Repeat) {
                    extensionSpace(row.last())
                }
            }
        }
    }
    
    private fun RDOMBuilder<DIV>.cardRowElement(editPoint: EditPoint) {
        val node = editPoint.node
        when (node) {
            is Action -> actionCard(editPoint)
            is Repeat -> repeatBlock(editPoint)
            else -> TODO()
        }
    }
}

fun RBuilder.cardSequence(elements: List<EditPoint>) = child(SequenceEditor::class) {
    attrs.elements = elements
}

private class ProgramEditor(props: Props) : RComponent<Props, RState>(props) {
    interface Props : RProps {
        var program: Seq
    }
    
    override fun RBuilder.render() {
        cardSequence(props.program.editPoints())
    }
}


fun RBuilder.programEditor(edited: Seq) = child(ProgramEditor::class) {
    attrs.program = edited
}
