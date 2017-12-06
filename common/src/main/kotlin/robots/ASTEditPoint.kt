package robots

class ASTEditPoint(
    val program: Seq,
    val path: ASTPath,
    val node: AST
) {
    operator fun contains(that: ASTEditPoint) =
        this.path.contains(that.path)
    
    fun remove(): Seq =
        program.removeAt(path)
    fun replaceWith(newAST: AST): Seq =
        program.replaceAt(path, newAST)
    fun insertBefore(newAST: AST): Seq =
        program.insertBefore(path, newAST)
    fun insertAfter(newAST: AST): Seq =
        program.insertAfter(path, newAST)
    
    fun replaceBranch(branchIndex: Int, newBranch: PList<AST>): Seq =
        program.replaceBranchAt(path, branchIndex, newBranch)
    
    fun moveTo(destination: ASTEditPoint, splice: Seq.(ASTPath, AST) -> Seq): Seq =
        program.move(path, destination.path, splice)
}


fun Seq.editPoints(): List<ASTEditPoint> =
    steps.mapIndexed { index, node -> ASTEditPoint(this, pathOf(0, index), node) }

fun ASTEditPoint.children(): List<ASTEditPoint> {
    val node = this.node
    return when (node) {
        is Action -> emptyList()
        is Repeat -> this.childEditPoints(0, node.repeated)
        is Seq -> this.childEditPoints(0, node.steps)
    }
}

private fun ASTEditPoint.childEditPoints(branchIndex: Int, children: PList<AST>): List<ASTEditPoint> =
    children.mapIndexed { index, node -> ASTEditPoint(program, path + ChildRef(branchIndex, index), node) }
