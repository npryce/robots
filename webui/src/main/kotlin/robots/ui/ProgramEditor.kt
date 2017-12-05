package robots.ui

import dnd.draggable
import dnd.dropTarget
import kotlinx.html.DIV
import kotlinx.html.title
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

private fun RBuilder.cardFace(deck: Deck, editor: EditPoint) {
    cardFace(deck, editor.node)
}

fun RBuilder.cardFace(deck: Deck, value: AST) {
    val style = deck.styleFor(value)
    div("card ${style.category}") {
        attrs.title = style.explanation
        +style.face
    }
}

fun RBuilder.actionCard(deck: Deck, editor: EditPoint) {
    draggable(dataProvider = { editor }) {
        cardFace(deck, editor)
    }
}

fun RBuilder.controlCard(deck: Deck, editor: EditPoint) {
    cardFace(deck, editor)
}

fun RDOMBuilder<DIV>.repeatBlock(deck: Deck, editPoint: EditPoint, onEdit: (Seq) -> Unit) {
    div("cardblock") {
        controlCard(deck, editPoint)
        
        val childEditPoints = editPoint.children()
        
        if (childEditPoints.isEmpty()) {
            startingSpace(editPoint, 0, onEdit)
        }
        else {
            cardSequence(deck, childEditPoints, onEdit)
        }
    }
}

fun RBuilder.cardSequence(deck: Deck, elements: List<EditPoint>, onEdit: (Seq) -> Unit) {
    fun RDOMBuilder<DIV>.cardRowElement(editPoint: EditPoint) {
        val node = editPoint.node
        when (node) {
            is Action -> actionCard(deck, editPoint)
            is Repeat -> repeatBlock(deck, editPoint, onEdit)
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
        val rows = elements.toRows()
        rows.forEach { row -> cardRow(row) }
        
        val last = rows.last().last()
        if (last.node is Repeat) {
            div("cardrow") {
                extensionSpace(last, onEdit)
            }
        }
    }
}

private fun List<EditPoint>.toRows() =
    flattenImmediateSequences()
        .splitAfter { it.node is Repeat }

fun List<EditPoint>.flattenImmediateSequences(): List<EditPoint> =
    flatMap { if (it.node is Seq) it.children() else listOf(it) }

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

fun RBuilder.programEditor(deck: Deck, program: Seq, onEdit: (Seq) -> Unit) {
    div("program") {
        if (program.steps.isEmpty()) {
            firstElementSpace(program, onEdit)
        }
        else {
            cardSequence(deck, program.editPoints(), onEdit)
        }
    }
}
