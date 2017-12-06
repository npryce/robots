package robots.ui

import dnd.draggable
import dnd.dropTarget
import kotlinx.html.DIV
import kotlinx.html.title
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.div
import robots.AST
import robots.ASTEditPoint
import robots.Action
import robots.Repeat
import robots.Seq
import robots.children
import robots.editPoints
import robots.insertAfter
import robots.isEmpty
import robots.pListOf
import robots.splitAfter
import robots.withSteps


fun RBuilder.extensionSpace(editor: ASTEditPoint, onEdit: (Seq) -> Unit) {
    fun canAccept(dragged: Any) =
        when (dragged) {
            is AST -> true
            is ASTEditPoint -> editor !in dragged
            else -> false
        }
    
    fun accept(dropped: Any) {
        when (dropped) {
            is AST -> {
                val newProgram = editor.insertAfter(dropped)
                onEdit(newProgram)
            }
            is ASTEditPoint -> {
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

fun RBuilder.startingSpace(editor: ASTEditPoint, branch: Int, onEdit: (Seq) -> Unit) {
    fun canAccept(dragged: Any) =
        when (dragged) {
            is AST -> true
            is ASTEditPoint -> editor !in dragged
            else -> false
        }
    
    fun accept(dropped: Any) {
        when (dropped) {
            is AST -> {
                val branchIndex = 0
                val newBranch = pListOf(dropped)
    
                val newProgram = editor.replaceBranch(branchIndex, newBranch)
                onEdit(newProgram)
            }
            is ASTEditPoint -> {
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

private fun RBuilder.cardFace(deck: Deck, editor: ASTEditPoint) {
    cardFace(deck, editor.node)
}

fun RBuilder.cardFace(deck: Deck, value: AST) {
    val style = deck.styleFor(value)
    div("card ${style.category}") {
        attrs.title = style.explanation
        +style.face
    }
}

fun RBuilder.actionCard(deck: Deck, editor: ASTEditPoint) {
    draggable(dataProvider = { editor }) {
        cardFace(deck, editor)
    }
}

fun RBuilder.controlCard(deck: Deck, editor: ASTEditPoint) {
    cardFace(deck, editor)
}

fun RDOMBuilder<DIV>.repeatBlock(deck: Deck, editPoint: ASTEditPoint, onEdit: (Seq) -> Unit) {
    draggable(dataProvider = {editPoint}) {
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
}

fun RBuilder.cardSequence(deck: Deck, elements: List<ASTEditPoint>, onEdit: (Seq) -> Unit) {
    fun RDOMBuilder<DIV>.cardRowElement(editPoint: ASTEditPoint) {
        val node = editPoint.node
        when (node) {
            is Action -> actionCard(deck, editPoint)
            is Repeat -> repeatBlock(deck, editPoint, onEdit)
            else -> TODO()
        }
    }
    
    fun RDOMBuilder<DIV>.cardRow(row: List<ASTEditPoint>) {
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

private fun List<ASTEditPoint>.toRows() =
    flattenImmediateSequences()
        .splitAfter { it.node is Repeat }

fun List<ASTEditPoint>.flattenImmediateSequences(): List<ASTEditPoint> =
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
