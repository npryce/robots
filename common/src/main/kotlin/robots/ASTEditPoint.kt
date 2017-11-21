package robots


interface EditPoint {
    fun displayId(): String
    
    fun remove(): Seq
    fun replaceWith(newAST: AST): Seq
    fun insertBefore(newAST: AST): Seq
}

class PListElementEditPoint(
    private val element: PListZipper<AST>,
    private val replaceInProgram: (PList<AST>) -> Seq
) : EditPoint {
    override fun displayId() = element.current.displayId()
    
    override fun remove() = replaceInProgram(element.remove()?.toPList()?: emptyPList())
    override fun replaceWith(newAST: AST) = apply { replaceWith(newAST) }
    override fun insertBefore(newAST: AST) = apply { insert(newAST) }
    
    private fun apply(action: PListZipper<AST>.() -> PListZipper<AST>) =
        replaceInProgram(element.action().toPList())
}

fun PList<AST>.editPoints(replaceInProgram: (PList<AST>) -> Seq): List<EditPoint> =
    generateSequence(zipper(), { it.next() })
        .map { zipper -> PListElementEditPoint(zipper, replaceInProgram) }
        .toList()

fun Seq.editPoints() =
    steps.editPoints { copy(steps = it) }

private fun AST.displayId(): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
