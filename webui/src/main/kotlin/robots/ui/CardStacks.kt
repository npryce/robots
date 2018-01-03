package robots.ui

import dnd.draggable
import dnd.dropTarget
import react.RBuilder
import react.dom.div
import robots.AST
import robots.ASTEditPoint
import robots.Seq


fun RBuilder.cardStack(deck: Deck, value: AST) {
    draggable(dataProvider = { value }) {
        cardFace(deck, value)
    }
}

private fun RBuilder.cardStackRow(deck: Deck, instructions: Iterable<AST>) {
    div("stackrow") {
        instructions.forEach { i -> cardStack(deck, i) }
    }
}

fun RBuilder.cardStacks(deck: Deck, onEdit: (Seq) -> Unit) {
    fun canAccept(dragged: Any) =
        dragged is ASTEditPoint
    
    fun accept(dragged: Any) {
        if (dragged is ASTEditPoint) {
            onEdit(dragged.remove())
        }
    }
    
    dropTarget(canAccept = ::canAccept, accept = ::accept, classes = "stacks") {
        cardStackRow(deck, deck.actionCards.values)
        cardStackRow(deck, deck.repeatCards.values)
    }
}
