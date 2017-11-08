package robots

sealed class AST
data class Action(val name: String) : AST()
data class Repeat(val times: Int, val repeated: AST) : AST()
data class Seq(val steps: PList<AST>) : AST() {
    constructor(vararg steps: AST) : this(pListOf(*steps))
}


fun AST.toCompactString(): String = when (this) {
    is Action -> name
    is Repeat -> times.toString() + " * " + repeated.toCompactString()
    is Seq -> "[" + steps.toCompactString() + "]"
}

private fun PList<AST>.toCompactString(): String {
    return when (this) {
        is Cons<AST> -> head.toCompactString() + (if (tail == Empty) "" else "; ") + tail.toCompactString()
        Empty -> ""
    }
}
