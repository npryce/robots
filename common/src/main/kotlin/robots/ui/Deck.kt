package robots.ui

import robots.AST
import robots.Action
import robots.Repeat
import robots.Seq
import robots.replace
import robots.ui.CardCategory.action
import robots.ui.CardCategory.control

enum class CardCategory {
    action,
    control,
    invisible
}

interface CardStyle {
    val face: String
    val category: CardCategory
    val explanation: String
    val value: AST
}

fun CardStyle(face: String, value: Action) = ActionCardStyle(face, value)
fun CardStyle(value: Repeat) = RepeatCardStyle(value)


data class ActionCardStyle(override val face: String, override val value: Action) : CardStyle {
    override val category: CardCategory get() = action
    override val explanation: String get() = value.text
}

class RepeatCardStyle(override val value: Repeat) : CardStyle {
    override val face: String get() = "${value.times}×"
    override val category: CardCategory get() = control
    override val explanation: String get() = "Repeat ${value.times} times"
}


data class ActionCardPack(private val cards: List<ActionCardStyle>) : Iterable<ActionCardStyle> by cards {
    private val byValue = cards.associateBy { it.value }
    
    constructor(vararg cards: ActionCardStyle) : this(cards.toList())
    
    fun styleFor(action: Action): ActionCardStyle =
        byValue[action] ?: CardStyle(face = "?", value = action)
    
    fun replace(index: Int, replacement: ActionCardStyle) =
        copy(cards = cards.replace(index, replacement))
    
    fun remove(action: ActionCardStyle) =
        copy(cards = cards - action)
    
    fun add(newAction: ActionCardStyle) =
        copy(cards = cards + newAction)
}

private fun basicActionCards() = ActionCardPack(
    CardStyle(face = "⬆", value = Action("Step forwards")),
    CardStyle(face = "⬇", value = Action("Step backwards")),
    CardStyle(face = "«", value = Action("Turn to your left")),
    CardStyle(face = "»", value = Action("Turn to your right")),
    CardStyle(face = "☝️", value = Action("Pick up")),
    CardStyle(face = "👇", value = Action("Put down"))
)

data class Deck(
    val actionCards: ActionCardPack = basicActionCards(),
    val repeatCards: List<Repeat> = (2..10).map { n -> Repeat(n) }
) {
    fun replaceAction(index: Int, replacement: ActionCardStyle) =
        copy(actionCards = actionCards.replace(index, replacement))
    
    fun addAction(newAction: ActionCardStyle) =
        copy(actionCards = actionCards.add(newAction))
    
    fun removeAction(action: ActionCardStyle) =
        copy(actionCards = actionCards.remove(action))
    
    fun styleFor(value: AST): CardStyle =
        when (value) {
            is Action -> actionCards.styleFor(value)
            is Repeat -> CardStyle(value)
            is Seq -> throw IllegalArgumentException("Seq is not displayed as a card")
        }
}
