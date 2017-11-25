package robots.ui

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
import robots.editPoints
import robots.splitAfter
import robots.ui.ProgramEditor.Props


interface CardProps : RProps {
    var editor: EditPoint
}

class ExtensionSpace(props: ExtensionSpace.Props) : RComponent<ExtensionSpace.Props, RState>(props) {
    interface Props : CardProps
    
    override fun RBuilder.render() {
        div("cursor") {}
    }
}

fun RBuilder.extensionSpace(editor: EditPoint) = child(ExtensionSpace::class) {
    attrs.editor = editor
}


class ActionCard(props: CardProps) : RComponent<CardProps, RState>(props) {
    override fun RBuilder.render() {
        div("card action") { +props.editor.displayId() }
    }
}

fun RBuilder.actionCard(editor: EditPoint) = child(ActionCard::class) {
    attrs.editor = editor
}

class ControlCard(props: CardProps) : RComponent<CardProps, RState>(props) {
    override fun RBuilder.render() {
        div("card control") { +props.editor.displayId() }
    }
}

fun RBuilder.controlCard(editor: EditPoint) = child(ControlCard::class) {
    attrs.editor = editor
}


class SequenceEditor(props: Props) : RComponent<SequenceEditor.Props, RState>(props) {
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
            is Repeat -> repeatBlock(node, editPoint)
            else -> TODO()
        }
    }
    
    private fun RDOMBuilder<DIV>.repeatBlock(repeat: Repeat, editPoint: EditPoint) {
        div("cardblock") {
            controlCard(editPoint)
            cardSequence(repeat.repeated.editPoints(editPoint, { repeat.copy(repeated = it) }))
        }
    }
}

fun RBuilder.cardSequence(elements: List<EditPoint>) = child(SequenceEditor::class) {
    attrs.elements = elements
}

class ProgramEditor(props: Props) : RComponent<Props, RState>(props) {
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
