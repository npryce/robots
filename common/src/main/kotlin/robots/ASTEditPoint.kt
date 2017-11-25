package robots

interface EditPoint {
    val parent: EditPoint?
    val node: AST
    
    fun remove(): Seq
    fun replaceWith(newAST: AST): Seq
    fun insertBefore(newAST: AST): Seq
    fun insertAfter(newAST: AST): Seq
}

abstract class AbstractEditPoint: EditPoint {
    protected abstract val focus: PListFocus<AST>
    override val node: AST get() = focus.current
    
    override fun remove() = applyModifier { it.remove() }
    override fun replaceWith(newAST: AST) = applyModifier { it.replaceWith(newAST) }
    override fun insertBefore(newAST: AST) = applyModifier { it.insertBefore(newAST) }
    override fun insertAfter(newAST: AST) = applyModifier { it.insertAfter(newAST) }
    
    private fun applyModifier(modifier: (PListFocus<AST>) -> PListFocus<AST>?) =
        splice(modifier(focus)?.toPList() ?: emptyPList())
    
    protected abstract fun splice(newSteps: PList<AST>): Seq
}

class TopLevelEditPoint(
    private val program: Seq,
    override val focus: PListFocus<AST>
) : AbstractEditPoint() {
    
    override val parent = null
    
    override fun splice(newSteps: PList<AST>) =
        program.copy(steps = newSteps)
}

class InternalEditPoint(
    override val parent: EditPoint,
    override val focus: PListFocus<AST>,
    private val replaceChildren: (PList<AST>) -> AST
) : AbstractEditPoint() {
    
    override fun splice(newSteps: PList<AST>) =
        parent.replaceWith(replaceChildren(newSteps))
}


fun Seq.editPoints(): List<EditPoint> =
    steps.focusElements()
        .map { focus -> TopLevelEditPoint(this, focus) }

fun EditPoint.children(): List<EditPoint> {
    val node = this.node
    return when (node) {
        is Action -> emptyList()
        is Repeat -> node.repeated.editPoints(this, { node.copy(repeated = it) })
        is Seq -> node.steps.editPoints(this, { node.copy(steps = it) })
    }
}

private fun PList<AST>.editPoints(parent: EditPoint, replaceInParent: (PList<AST>) -> AST): List<EditPoint> =
    focusElements()
        .map { zipper -> InternalEditPoint(parent, zipper, replaceInParent) }
