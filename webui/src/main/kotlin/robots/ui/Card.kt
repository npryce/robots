package robots.ui

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import robots.EditPoint
import robots.ui.Card.Props

class Card(props: Props) : RComponent<Props, RState>(props) {
    interface Props : RProps {
        var editor: EditPoint
    }
    
    override fun RBuilder.render() {
        div("card action") { +props.editor.displayId() }
    }
}

fun RBuilder.card(editor: EditPoint) = child(Card::class) {
    attrs.editor = editor
}
