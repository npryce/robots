@file:JsModule("emoji-mart")
package vendor

import org.w3c.dom.events.Event
import react.RProps
import react.RState
import react.React


@JsName("Picker")
external class EmojiPicker: React.Component<EmojiPickerProps, RState> {
    override fun render()
}


external interface EmojiPickerProps : RProps {
    var autoFocus: Boolean      // false	        Auto focus the search input when mounted
    var color: String           // #ae65c5	        The top bar anchors select and hover color
    var emoji: String           // department_store	The emoji shown when no emojis are hovered, set to an empty string to show nothing
    var include: Array<String>  // []	            Only load included categories. Accepts I18n categories keys. Order will be respected, except for the recent category which will always be the first.
    var exclude: Array<String>  // []	            Don't load excluded categories. Accepts I18n categories keys.
    var recent: Array<String>   // []               Pass your own frequently used emojis as array of string IDs
    var emojiSize: Int          // 24               The emoji width and height
    var perLine: Int            // 9                Number of emojis per line. While there’s no minimum or maximum, this will affect the picker’s width. This will set Frequently Used length as well (perLine * 4)
    var native: Boolean         // false            Renders the native unicode emoji
    var set: String             // apple            The emoji set: 'apple', 'google', 'twitter', 'emojione', 'messenger', 'facebook'
    var sheetSize: Int          // 64               The emoji sheet size: 16, 20, 32, 64
    var showPreview: Boolean    // true             Display preview section
    var emojiTooltip: Boolean   // false	        Show emojis short name when hovering (title)
    var skin: Int               // 1                Default skin color: 1, 2, 3, 4, 5, 6
    var style: String           //                  Inline styles applied to the root element. Useful for positioning
    var title: String           // Emoji Mart™	    The title shown when no emojis are hovered
    
    var onClick: (Emoji, Event) -> Unit
    
    /**
     *  A Fn to choose whether an emoji should be displayed or not
     */
    var emojisToShowFilter: (Emoji) -> Boolean
    
    
    /*
    custom		[]	Custom emojis
    i18n		{…}	An object containing localized strings
    backgroundImageFn		((set, sheetSize) => …)	A Fn that returns that image sheet to use for emojis. Useful for avoiding a request if you have the sheet locally.
     */
}

external interface Emoji {
    var id: String
    var name: String
    var colons: String
    var text : String
    var emoticons: Array<String>
    var skin: Int?
    var native: String?
}

