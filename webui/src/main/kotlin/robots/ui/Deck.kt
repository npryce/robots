package robots.ui

import robots.AST
import robots.Action
import robots.Repeat
import robots.Seq

fun cardCategoryClass(instruction: AST) =
    when (instruction) {
        is Action -> "action"
        is Repeat -> "control"
        is Seq -> "invisible"
    }


data class StyledAction(val face: String, val action: Action)

class ActionCardPack(private val cards: List<StyledAction>) : Iterable<StyledAction> by cards {
    private val byValue = cards.associateBy { it.action }
    
    constructor(vararg cards: StyledAction) : this(cards.toList())
    
    fun styleFor(action: Action): StyledAction =
        byValue[action] ?: StyledAction(face = "?", action = action)
}


private fun basicActionCards() = ActionCardPack(
    StyledAction(face = "⬆️", action = Action("step forwards")),
    StyledAction(face = "⬇️", action = Action("step backwards")),
    StyledAction(face = "⬅️", action = Action("turn to your left")),
    StyledAction(face = "➡️️", action = Action("turn to your right")),
    StyledAction(face = "👏", action = Action("clap")),
    StyledAction(face = "💩", action = Action("poop")),
    StyledAction(face = "🥊", action = Action("hit it"))
)


class Deck {
    val actionCards = basicActionCards()
    val repeatCards = (2..10).map { n -> Repeat(n) }
    
    fun cardFace(value: AST) =
        when (value) {
            is Action -> actionCards.styleFor(value).face
            is Repeat -> "${value.times}×"
            is Seq -> "[]"
        }
}
