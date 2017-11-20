package robots.ui

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.render
import react.dom.span
import kotlin.browser.document


interface WelcomeProps: RProps {
    var name: String
}

class Welcome(props: WelcomeProps): RComponent<WelcomeProps, RState>() {
    override fun RBuilder.render() {
        div {
            +"Hello, "; span("name") { + props.name }
        }
    }
}

fun RBuilder.welcome(name: String) = child(Welcome::class) {
    attrs.name = name
}

fun main(args: Array<String>) {
    val app = document.getElementById("app") ?: throw IllegalStateException("no element called 'app")
    
    render(app) {
        welcome("Nat")
    }
}
