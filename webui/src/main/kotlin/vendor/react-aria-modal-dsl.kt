package vendor

import react.RBuilder
import react.RHandler

fun RBuilder.ariaModal(conf: RHandler<ModalProps>) = child(Modal::class, conf)
