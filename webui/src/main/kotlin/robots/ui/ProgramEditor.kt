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
    
    dropTarget(::canAccept, ::accept, classes = "cursor")
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
                onEdit(editor.replaceBranch(0, pListOf(dropped)))
            }
            is ASTEditPoint -> {
                if (editor !in dropped) {
                    onEdit(dropped.moveToNewBranch(editor, branch))
                }
            }
        }
    }
    
    dropTarget(::canAccept, ::accept, classes = "cursor required")
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

fun RBuilder.actionCard(deck: Deck, editor: ASTEditPoint, isEditable: Boolean) {
    draggable(dataProvider = { editor }, disabled = !isEditable) {
        cardFace(deck, editor)
    }
}

fun RBuilder.controlCard(deck: Deck, editor: ASTEditPoint) {
    cardFace(deck, editor)
}

fun RBuilder.repeatBlock(deck: Deck, editPoint: ASTEditPoint, isEditable: Boolean, onEdit: (Seq) -> Unit) {
    draggable(dataProvider = { editPoint }, disabled = !isEditable) {
        div("cardblock") {
            controlCard(deck, editPoint)
            
            val childEditPoints = editPoint.children()
            
            if (childEditPoints.isEmpty() && isEditable) {
                startingSpace(editPoint, 0, onEdit)
            }
            else {
                cardRows(deck, childEditPoints, isEditable, onEdit)
            }
        }
    }
}

fun RBuilder.seqBlock(deck: Deck, editPoint: ASTEditPoint, isEditable: Boolean, onEdit: (Seq) -> Unit) {
    cardRows(deck, editPoint.children(), isEditable, onEdit)
}

fun RBuilder.cardRowElement(deck: Deck, editPoint: ASTEditPoint, isEditable: Boolean, onEdit: (Seq) -> Unit) {
    val node = editPoint.node
    when (node) {
        is Action -> actionCard(deck, editPoint, isEditable)
        is Repeat -> repeatBlock(deck, editPoint, isEditable, onEdit)
        is Seq -> seqBlock(deck, editPoint, isEditable, onEdit)
    }
}

fun RDOMBuilder<DIV>.cardRow(deck: Deck, row: List<ASTEditPoint>, isEditable: Boolean, onEdit: (Seq) -> Unit) {
    div("cardrow") {
        row.forEach { editPoint -> cardRowElement(deck, editPoint, isEditable, onEdit) }
        
        val last = row.lastOrNull()
        if (last != null) {
            if (last.node !is Repeat && isEditable) {
                extensionSpace(row.last(), onEdit)
            }
        }
    }
}

fun RBuilder.cardRows(deck: Deck, elements: List<ASTEditPoint>, isEditable: Boolean, onEdit: (Seq) -> Unit) {
    div("cardrows") {
        val rows = elements.toRows()
        rows.forEach { row -> cardRow(deck, row, isEditable, onEdit) }
        
        val last = rows.last().last()
        if (last.node is Repeat && isEditable) {
            div("cardrow") {
                extensionSpace(last, onEdit)
            }
        }
    }
}

private fun List<ASTEditPoint>.toRows() =
    splitAfter { it.node is Repeat || it.node is Seq }

fun RBuilder.firstElementSpace(program: Seq, isEditable: Boolean, onEdit: (Seq) -> Unit) {
    fun canAccept(dragged: Any) =
        dragged is AST
    
    fun accept(dropped: Any) {
        if (dropped is AST) onEdit(program.withSteps(dropped))
    }
    
    div("cardsequence") {
        div("cardrow") {
            dropTarget(canAccept = { canAccept(it) }, accept = { accept(it) }, classes = "cursor", disabled = !isEditable) {
                if (isEditable) attrs.classes += " required"
            }
            
            if (isEditable) {
                tip("Drag a card from the stacks below and drop it onto here")
            }
        }
    }
}

fun RBuilder.programEditor(deck: Deck, program: Seq, isEditable: Boolean, onEdit: (Seq) -> Unit = {}) {
    div("program") {
        if (program.steps.isEmpty()) {
            firstElementSpace(program, isEditable, onEdit)
        }
        else {
            cardRows(deck, program.editPoints(), isEditable, onEdit)
        }
    }
}

fun RBuilder.tip(text: String) {
    div("tip") { +text }
}
