package robots

interface EditPoint {
    val node: AST
    
    fun displayId(): String = node.displayId()
    
    fun remove(): Seq
    fun replaceWith(newAST: AST): Seq
    fun insertBefore(newAST: AST): Seq
}

class PListElementEditPoint(
    private val element: PListFocus<AST>,
    private val replaceInProgram: (PList<AST>) -> Seq
) : EditPoint {
    
    override val node: AST get() = element.current
    
    override fun remove() = replaceInProgram(element.remove()?.toPList() ?: emptyPList())
    override fun replaceWith(newAST: AST) = apply { replaceWith(newAST) }
    override fun insertBefore(newAST: AST) = apply { insertBefore(newAST) }
    
    private fun apply(action: PListFocus<AST>.() -> PListFocus<AST>) =
        replaceInProgram(element.action().toPList())
}

fun PList<AST>.editPoints(replaceInProgram: (PList<AST>) -> Seq): List<EditPoint> =
    generateSequence(zipper(), { it.next() })
        .map { zipper -> PListElementEditPoint(zipper, replaceInProgram) }
        .toList()

fun Seq.editPoints() =
    steps.editPoints { copy(steps = it) }

private fun AST.displayId() =
    when (this) {
        is Action -> name
        is Repeat -> "${times}×"
        is Seq -> "[]"
    }
