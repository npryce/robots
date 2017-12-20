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


data class CardSuit<Style: CardStyle>(private val cards: List<Style>) : Iterable<Style> by cards {
    private val byValue = cards.associateBy { it.value }
    
    constructor(vararg cards: Style) : this(cards.toList())
    
    operator fun get(action: AST) = byValue[action]
    
    val values: Iterable<AST> get() = cards.map { it.value }
    
    fun replace(index: Int, replacement: Style) =
        copy(cards = cards.replace(index, replacement))
    
    fun remove(action: Style) =
        copy(cards = cards - action)
    
    fun add(newAction: Style) =
        copy(cards = cards + newAction)
}

typealias ActionCardSuit = CardSuit<ActionCardStyle>
typealias RepeatCardSuit = CardSuit<RepeatCardStyle>

fun ActionCardSuit.styleFor(action: Action): ActionCardStyle =
    get(action) ?: CardStyle(face = "?", value = action)

fun RepeatCardSuit.styleFor(repeat: Repeat): RepeatCardStyle =
    get(repeat) ?: CardStyle(repeat)

private fun basicActionCards() = ActionCardSuit(
    CardStyle(face = "⬆", value = Action("Step forwards")),
    CardStyle(face = "⬇", value = Action("Step backwards")),
    CardStyle(face = "«", value = Action("Turn to your left")),
    CardStyle(face = "»", value = Action("Turn to your right")),
    CardStyle(face = "☝️", value = Action("Pick up")),
    CardStyle(face = "👇", value = Action("Put down"))
)

private fun basicRepeatCards() =
    RepeatCardSuit((2..10).map { n -> CardStyle(Repeat(n)) })


data class Deck(
    val actionCards: ActionCardSuit = basicActionCards(),
    val repeatCards: RepeatCardSuit = basicRepeatCards()
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
            is Repeat -> repeatCards.styleFor(value)
            is Seq -> throw IllegalArgumentException("Seq is not displayed as a card")
        }
}

