/*
 * Works around poor definitions of events in the Kotlin React bindings
 */
package robots.ui

import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.InputEvent

fun <E: Event> handler(fn: (E)->Unit): (Event)->Unit = { e ->
    @Suppress("UnsafeCastFromDynamic")
    fn(e.asDynamic().unsafeCast<E>())
}

val InputEvent.newValue: String get() =
    target.unsafeCast<HTMLInputElement>().value
