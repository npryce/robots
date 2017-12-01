package robots.ui

import dnd.draggable
import dnd.dropTarget
import kotlinx.html.DIV
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.div
import robots.AST
import robots.Action
import robots.EditPoint
import robots.Repeat
import robots.Seq
import robots.children
import robots.editPoints
import robots.insertAfter
import robots.isEmpty
import robots.pListOf
import robots.replaceBranch
import robots.splitAfter
import robots.withSteps


fun RBuilder.extensionSpace(editor: EditPoint, onEdit: (Seq) -> Unit) {
    fun canAccept(dragged: Any) =
        when (dragged) {
            is AST -> true
            is EditPoint -> editor !in dragged
            else -> false
        }
    
    fun accept(dropped: Any) {
        when (dropped) {
            is AST -> {
                val newProgram = editor.insertAfter(dropped)
                onEdit(newProgram)
            }
            is EditPoint -> {
                if (editor !in dropped) {
                    val newProgram = dropped.moveTo(editor, Seq::insertAfter)
                    onEdit(newProgram)
                }
            }
        }
    }
    
    dropTarget(::canAccept, ::accept) {
        div("cursor") {}
    }
}

fun RBuilder.startingSpace(editor: EditPoint, branch: Int, onEdit: (Seq) -> Unit) {
    fun canAccept(dragged: Any) =
        when (dragged) {
            is AST -> true
            is EditPoint -> editor !in dragged
            else -> false
        }
    
    fun accept(dropped: Any) {
        when (dropped) {
            is AST -> {
                val newProgram = editor.replaceWith(editor.node.replaceBranch(0, pListOf(dropped)))
                onEdit(newProgram)
            }
            is EditPoint -> {
                if (editor !in dropped) {
                    TODO("dropping an existing card not supported yet")
                }
            }
        }
    }
    
    dropTarget(::canAccept, ::accept) {
        div("cursor required") {}
    }
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
        
        val childEditPoints = editPoint.children()
        
        if (childEditPoints.isEmpty()) {
            startingSpace(editPoint, 0, onEdit)
        }
        else {
            cardSequence(childEditPoints, onEdit)
        }
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
        elements
            .splitAfter { it.node is Repeat }
            .forEach { row -> cardRow(row) }
    }
}

fun RBuilder.firstElementSpace(program: Seq, onEdit: (Seq) -> Unit) {
    fun canAccept(dragged: Any) =
        dragged is AST
    
    fun accept(dropped: Any) {
        if (dropped is AST) onEdit(program.withSteps(dropped))
    }
    
    dropTarget(canAccept = ::canAccept, accept = ::accept) {
        div("cardsequence") {
            div("cardrow") {
                div("cursor required") {}
                div("tip") {
                    +"👈 Drag a card from the stacks below and drop it onto here"
                }
            }
        }
    }
}

fun RBuilder.programEditor(program: Seq, onEdit: (Seq) -> Unit) {
    div("program") {
        if (program.steps.isEmpty()) {
            firstElementSpace(program, onEdit)
        }
        else {
            cardSequence(program.editPoints(), onEdit)
        }
    }
}
