package robots

data class Trace(val action: Action?, val next: Seq?, val past: Trace?)

fun start(seq: Seq) = Trace(null, seq, null)

fun Trace.isFinished() =
    next == null

fun Trace.next(reduce: (Seq) -> Reduction = Seq::reduceToAction) = when (next) {
    null -> this
    else -> reduce(next).let { (_, action, future) -> Trace(action, future, this) }
}

tailrec fun Trace.run(reduce: (Seq) -> Reduction = Seq::reduceToAction): Trace =
    if (isFinished()) this else next(reduce).run(reduce)

fun Trace.toSeq(): Seq =
    Seq(actionsThen(next?.steps ?: Empty))

private tailrec fun Trace.actionsThen(tail: PList<AST>): PList<AST> {
    val longerTail = if (action != null) Cons(action, tail) else tail
    return if (past == null) longerTail else past.actionsThen(longerTail)
}
