package robots

import kotlin.math.min

data class ChildRef(val branch: Int, val element: Int) : Comparable<ChildRef> {
    override fun compareTo(other: ChildRef) =
        compareValuesBy(this, other, ChildRef::branch, ChildRef::element)
    
}

typealias ASTPath = List<ChildRef>

fun pathOf(vararg indices: Int): ASTPath = indices.toList().chunked(2) { (a, b) -> ChildRef(a, b) }
fun emptyPath(): ASTPath = emptyList()

operator fun ASTPath.compareTo(that: ASTPath): Int {
    (0 until min(this.size, that.size)).forEach { i ->
        when {
            this[i] > that[i] -> return 1
            this[i] < that[i] -> return -1
        }
    }
    return this.size.compareTo(that.size)
}

operator fun ASTPath.contains(that: ASTPath): Boolean =
    this.size <= that.size && this == that.subList(0, this.size)

operator fun Seq.get(path: ASTPath): AST? =
    get(path, 0)

operator fun Seq.get(vararg pathElements: Int): AST? =
    get(pathOf(*pathElements))

private tailrec fun AST.get(path: ASTPath, pathIndex: Int, maxPathIndex: Int = path.size - 1): AST? {
    if (pathIndex >= path.size) {
        return this
    }
    else {
        val (childrenIndex, childIndex) = path.getOrNull(pathIndex) ?: return null
        val child = branch(childrenIndex)?.elementAt(childIndex) ?: return null
        
        return child.get(path, pathIndex + 1, maxPathIndex)
    }
}

private fun AST.branch(index: Int): PList<AST>? =
    when (this) {
        is Action -> null
        is Seq -> branch(index)
        is Repeat -> branch(index)
    }

fun AST.replaceBranch(index: Int, newBranch: PList<AST>): AST =
    when (this) {
        is Action -> this
        is Seq -> replaceBranch(index, newBranch)
        is Repeat -> replaceBranch(index, newBranch)
    }

private fun Seq.branch(index: Int) = steps.takeIf { index == 0 }

private fun Seq.replaceBranch(n: Int, newBranch: PList<AST>): Seq =
    if (n == 0) copy(steps = newBranch) else this

private fun Repeat.branch(index: Int) = repeated.takeIf { index == 0 }

private fun Repeat.replaceBranch(n: Int, newBranch: PList<AST>): Repeat =
    if (n == 0) copy(repeated = newBranch) else this

fun Seq.removeAt(path: ASTPath): Seq {
    return applyAt(path) { it.remove() }
}

fun Seq.replaceAt(path: ASTPath, newElement: AST): Seq {
    return applyAt(path) { it.replaceWith(newElement) }
}

fun Seq.insertAfter(path: ASTPath, newElement: AST): Seq {
    return applyAt(path) { it.insertAfter(newElement) }
}

fun Seq.insertBefore(path: ASTPath, newElement: AST): Seq {
    return applyAt(path) { it.insertBefore(newElement) }
}

fun Seq.move(from: ASTPath, to: ASTPath, splice: Seq.(ASTPath, AST) -> Seq): Seq {
    val srcNode = this[from] ?: return this
    return if (from < to)
        splice(to, srcNode).removeAt(from)
    else
        removeAt(from).splice(to, srcNode)
}

private operator fun AST.get(childRef: ChildRef): PListFocus<AST>? =
    branch(childRef.branch)?.focusNth(childRef.element)

private class Stitch(val parent: AST, val childRef: ChildRef, val childFocus: PListFocus<AST>)

private inline fun Seq.applyAt(path: ASTPath, modifyBranch: (PListFocus<AST>) -> PListFocus<AST>?): Seq {
    val childRef = path.firstOrNull() ?: return this
    val childFocus: PListFocus<AST> = this[childRef] ?: return this
    
    val stitches = generateSequence(0 to Stitch(this, childRef, childFocus)) { (lastPathIndex, lastStitch) ->
        val nextPathIndex = lastPathIndex + 1
        path.getOrNull(nextPathIndex)?.let { childRef ->
            val nextParent = lastStitch.childFocus.current
            nextParent[childRef]?.let { childFocus ->
                nextPathIndex to Stitch(nextParent, childRef, childFocus)
            }
        }
    }.map { it.second }.toList().reversed().dropLast(1)
    
    // Eugh!  This can be made more elegant
    return if (stitches.isEmpty()) {
        this.replaceBranch(childRef.branch, modifyBranch(childFocus).toPList())
    } else {
        val lowestStitch = stitches.first()
        val newLowestBranch = modifyBranch(lowestStitch.childFocus)
        val modifiedLeaf = lowestStitch.parent.replaceBranch(lowestStitch.childRef.branch, newLowestBranch.toPList())
    
        val newChild: AST = stitch(modifiedLeaf, stitches.drop(1))
        val newBranch = childFocus.replaceWith(newChild).toPList()
    
        this.replaceBranch(childRef.branch, newBranch)
    }
}

private fun stitch(modifiedLeaf: AST, stitches: List<Stitch>): AST {
    return stitches.fold(modifiedLeaf, { child, stitch ->
        stitch.parent.replaceBranch(stitch.childRef.branch, stitch.childFocus.replaceWith(child).toPList())
    })
}


