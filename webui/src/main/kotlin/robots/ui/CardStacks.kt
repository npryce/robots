package robots.ui

import dnd.draggable
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import robots.AST
import robots.Action
import robots.Repeat


private class CardStack(props: CardStack.Props) : RComponent<CardStack.Props, RState>(props) {
    interface Props : RProps {
        var instruction: AST
    }
    
    override fun RBuilder.render() {
        draggable(dataProvider = {props.instruction}) {
            div("card ${cardCategoryClass(props.instruction)}") {
                +props.instruction.displayId()
            }
        }
    }
}

fun RBuilder.cardStack(instruction: AST) = child(CardStack::class) {
    attrs.instruction = instruction
}


private fun RBuilder.cardStackRow(instructions: Iterable<AST>) {
    div("stackrow") {
        instructions.forEach { i -> cardStack(i) }
    }
}

private fun RBuilder.cardStackRow(vararg instructions: AST) {
    cardStackRow(instructions.toList())
}

fun RBuilder.cardStacks() {
    div("stacks") {
        cardStackRow(Action("⬆️"), Action("⬇️"), Action("⬅️"), Action("➡️️"), Action("💩"), Action("💬"), Action("🥊"))
        cardStackRow((2..10).map { n -> Repeat(n) })
    }
}
