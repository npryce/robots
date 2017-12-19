package vendor

import org.w3c.dom.Element
import react.RProps
import react.RState
import react.React


external interface ModalProps : RProps {
    //    var underlayProps: {},
    var dialogId: String                  // 'react-aria-modal-dialog',
    var underlayClickExits: Boolean       // true,
    var escapeExits: Boolean              // true,
    var underlayColor: String             // 'rgba(0,0,0,0.5)',
    var includeDefaultStyles: Boolean     // true,
    var focusTrapPaused: Boolean          // false,
    var scrollDisabled: Boolean           // true
    
    // Either one of these
    var titleText: String                 // undefined
    var titleId: String                   // undefined
    
    // Either one of these
    var getApplicationNode: ()-> Element? // undefined
    var applicationNode: Element?         // undefined
    
    var onEnter: ()->Unit?                // undefined
    var onExit: ()->Unit?                 // undefined
}

@JsModule("react-aria-modal")
external class Modal : React.Component<ModalProps, RState> {
    override fun render()
}
