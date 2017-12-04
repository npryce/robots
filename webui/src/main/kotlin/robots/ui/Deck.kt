package robots.ui

import robots.AST
import robots.Action
import robots.Repeat
import robots.Seq
import robots.ui.CardCategory.action
import robots.ui.CardCategory.control
import robots.ui.CardCategory.invisible

enum class CardCategory {
    action,
    control,
    invisible
}

data class CardStyle(val face: String, val category: CardCategory, val explanation: String, val value: AST) {
    constructor(face: String, value: Action): this(face, action, value.name, value)
    constructor(value: Repeat): this("${value.times}×", control, "Repeat ${value.times} times", value)
}

class ActionCardPack(private val cards: List<CardStyle>) : Iterable<CardStyle> by cards {
    private val byValue = cards.associateBy { it.value }
    
    constructor(vararg cards: CardStyle) : this(cards.toList())
    
    fun styleFor(action: Action): CardStyle =
        byValue[action] ?: CardStyle(face = "?", value = action)
}


private fun basicActionCards() = ActionCardPack(
    CardStyle(face = "⬆️", value = Action("Step forwards")),
    CardStyle(face = "⬇️", value = Action("Step backwards")),
    CardStyle(face = "⬅️", value = Action("Turn to your left")),
    CardStyle(face = "➡️️", value = Action("Turn to your right")),
    CardStyle(face = "👏", value = Action("Clap")),
    CardStyle(face = "💩", value = Action("Poop")),
    CardStyle(face = "🥊", value = Action("Hit it"))
)


class Deck {
    val actionCards = basicActionCards()
    val repeatCards = (2..10).map { n -> Repeat(n) }
    
    fun styleFor(value: AST): CardStyle =
        when (value) {
            is Action -> actionCards.styleFor(value)
            is Repeat -> CardStyle(value)
            is Seq -> CardStyle("", invisible, "", value)
        }
}
