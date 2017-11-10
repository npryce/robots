package robots

sealed class AST
data class Action(val name: String) : AST()
data class Repeat(val times: Int, val repeated: Seq) : AST()
data class Seq(val steps: PList<AST>) : AST() {
    constructor(vararg steps: AST) : this(pListOf(*steps))
}

val nop = Seq(Empty)
