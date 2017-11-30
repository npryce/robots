package robots.ui

import dnd.draggable
import dnd.dropTarget
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
import robots.Repeat
import robots.Seq
import robots.children
import robots.editPoints
import robots.splitAfter


fun RBuilder.extensionSpace(editor: EditPoint, onEdit: (Seq) -> Unit) {
    fun canAccept(dragged: Any) =
        dragged is AST
    
    fun accept(dropped: Any) {
        val newProgram = editor.insertAfter(dropped as AST)
        onEdit(newProgram)
    }
    
    dropTarget(::canAccept, ::accept) {
        div("cursor") {}
    }
}

fun RBuilder.startingSpace() {
    div("cursor required") {}
}


fun RBuilder.actionCard(editor: EditPoint) {
    draggable(dataProvider = { editor }) {
        div("card action") { +editor.displayId() }
    }
}

fun RBuilder.controlCard(editor: EditPoint) {
    div("card control") { +editor.displayId() }
}

fun RDOMBuilder<DIV>.repeatBlock(editPoint: EditPoint, onEdit: (Seq) -> Unit) {
    div("cardblock") {
        controlCard(editPoint)
        cardSequence(editPoint.children(), onEdit)
    }
}

fun RBuilder.cardSequence(elements: List<EditPoint>, onEdit: (Seq) -> Unit) {
    fun RDOMBuilder<DIV>.cardRowElement(editPoint: EditPoint) {
        val node = editPoint.node
        when (node) {
            is Action -> actionCard(editPoint)
            is Repeat -> repeatBlock(editPoint, onEdit)
            else -> TODO()
        }
    }
    
    fun RDOMBuilder<DIV>.cardRow(row: List<EditPoint>) {
        div("cardrow") {
            row.forEach { editPoint -> cardRowElement(editPoint) }
            
            val last = row.lastOrNull()
            if (last != null) {
                if (last.node !is Repeat) {
                    extensionSpace(row.last(), onEdit)
                }
            }
        }
    }
    
    div("cardsequence") {
        if (elements.isEmpty()) {
            startingSpace()
        } else {
            elements
                .splitAfter { it.node is Repeat }
                .forEach { row -> cardRow(row) }
        }
    }
}


private class ProgramEditor(props: Props) : RComponent<ProgramEditor.Props, ProgramEditor.State>(props) {
    interface Props : RProps {
        var initialProgram: Seq
    }
    
    data class State(val program: Seq) : RState
    
    init {
        state = State(props.initialProgram)
    }
    
    override fun RBuilder.render() {
        cardSequence(state.program.editPoints(), onEdit = ::programEdited)
    }
    
    private fun programEdited(newProgramState: Seq) {
        setState({ State(program = newProgramState) })
    }
}


fun RBuilder.programEditor(edited: Seq) = child(ProgramEditor::class) {
    attrs.initialProgram = edited
}
