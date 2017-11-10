package robots

data class Trace(val action: Action?, val future: Seq?, val past: Trace?)

fun start(ast: Seq) = Trace(null, ast, null)

fun Trace.isFinished() =
    future == null

fun Trace.next(reduce: (Seq)->Reduction = Seq::reduceToAction) = when (future) {
    null -> this
    else -> reduce(future).let { (action, future) -> Trace(action, future, this) }
}

fun Trace.run(reduce: (Seq)->Reduction = Seq::reduceToAction): Trace {
    var current: Trace = this
    while (!current.isFinished()) current = current.next(reduce)
    return current
}
