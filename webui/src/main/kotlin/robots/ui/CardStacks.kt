package robots.ui

import dnd.draggable
import react.RBuilder
import react.dom.div
import robots.AST


fun RBuilder.cardStack(deck: Deck, value: AST) {
    draggable(dataProvider = { value }) {
        div("card ${cardCategoryClass(value)}") {
            +deck.cardFace(value)
        }
    }
}


private fun RBuilder.cardStackRow(deck: Deck, instructions: Iterable<AST>) {
    div("stackrow") {
        instructions.forEach { i -> cardStack(deck, i) }
    }
}

fun RBuilder.cardStacks(deck: Deck) {
    div("stacks") {
        cardStackRow(deck, deck.actionCards.map { it.action })
        cardStackRow(deck, deck.repeatCards)
    }
}
