package robots

sealed class AST
data class Action(val name: String) : AST()
data class Repeat(val times: Int, val repeated: PList<AST>) : AST() {
    constructor(times: Int, vararg steps: AST) : this(times, pListOf(*steps))
}
data class Seq(val steps: PList<AST>) : AST() {
    constructor(vararg steps: AST) : this(pListOf(*steps))
}

val nop = Seq(Empty)
