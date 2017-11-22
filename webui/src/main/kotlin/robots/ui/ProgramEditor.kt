package robots.ui

import kotlinx.html.DIV
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.RDOMBuilder
import react.dom.div
import robots.AST
import robots.Action
import robots.EditPoint
import robots.PList
import robots.Repeat
import robots.Seq
import robots.editPoints
import robots.splitAfter
import robots.ui.ProgramEditor.Props

interface CardProps: RProps {
    var editor: EditPoint
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
        var sequence: PList<AST>
        var replaceInProgram: (PList<AST>)->Seq
    }
    
    override fun RBuilder.render() {
        div("cardsequence") {
            props.sequence.editPoints(props.replaceInProgram)
                .splitAfter { it.node is Repeat }
                .forEach { row -> cardRow(row) }
        }
    }
    
    private fun RDOMBuilder<DIV>.cardRow(row: List<EditPoint>) {
        div("cardrow") {
            row.forEach { editPoint -> cardRowElement(editPoint) }
        }
    }
    
    private fun RDOMBuilder<DIV>.cardRowElement(editPoint: EditPoint) {
        val node = editPoint.node
        when(node) {
            is Action -> actionCard(editPoint)
            is Repeat -> repeatBlock(node, editPoint)
            else -> TODO()
        }
    }
    
    private fun RDOMBuilder<DIV>.repeatBlock(repeat: Repeat, editPoint: EditPoint) {
        div("cardblock") {
            controlCard(editPoint)
            cardSequence(repeat.repeated)
        }
    }
}

fun RBuilder.cardSequence(sequence: PList<AST>) = child(SequenceEditor::class) {
    attrs.sequence = sequence
}

class ProgramEditor(props: Props) : RComponent<Props, RState>(props) {
    interface Props : RProps {
        var program: Seq
    }
    
    override fun RBuilder.render() {
        cardSequence(props.program.steps)
    }
}


fun RBuilder.programEditor(edited: Seq) = child(ProgramEditor::class) {
    attrs.program = edited
}
