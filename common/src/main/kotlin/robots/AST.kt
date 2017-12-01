package robots

sealed class AST
data class Action(val name: String) : robots.AST()
data class Repeat(val times: Int, val repeated: PList<robots.AST>) : robots.AST() {
    constructor(times: Int, vararg steps: robots.AST) : this(times, pListOf(*steps))
}
data class Seq(val steps: PList<robots.AST>) : robots.AST() {
    constructor(vararg steps: robots.AST) : this(pListOf(*steps))
}

val nop = robots.Seq(Empty)

fun Seq.withSteps(vararg steps: AST) = copy(steps = pListOf(*steps))

