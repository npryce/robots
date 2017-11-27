package browser

import org.w3c.dom.Element
import org.w3c.dom.events.UIEvent

typealias TouchId = Int

external interface Touch {
    val identifier: TouchId
    val target: Element
    val screenX: Double
    val screenY: Double
    val clientX: Double
    val clientY: Double
    val pageX: Double
    val pageY: Double
}

external open class TouchList {
    val length: Int
    fun item(index: Int): Touch?
}

operator fun TouchList.get(n: Int) =
    item(n) ?: throw IndexOutOfBoundsException("index out of bounds: $n, length: $length")

external open class TouchEvent : UIEvent {
    val shiftKey: Boolean;
    val ctrlKey: Boolean;
    val altKey: Boolean;
    val metaKey: Boolean;
    
    /**
     * See [DOM Level 3 Events spec](https://www.w3.org/TR/uievents-key/#keys-modifier). for a list of valid (case-sensitive) arguments to this method.
     */
    fun getModifierState(key: String): Boolean;
    
    val touches: TouchList;
    val changedTouches: TouchList;
    val targetTouches: TouchList;
}
