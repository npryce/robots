package robots

fun AST.toCompactString(): String = when (this) {
    is Action -> name
    is Repeat -> times.toString() + "×" + repeated.toCompactString()
    is Seq -> "[" + steps.toCompactString() + "]"
}

private fun PList<AST>.toCompactString(): String {
    return when (this) {
        is Cons<AST> -> head.toCompactString() + (if (tail == Empty) "" else ", ") + tail.toCompactString()
        Empty -> ""
    }
}
