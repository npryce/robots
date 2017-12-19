package vendor

import react.RBuilder
import react.RHandler

fun RBuilder.emojiPicker(conf: RHandler<EmojiPickerProps>) = child(EmojiPicker::class, conf)
