package robots

data class Trace(val action: Action?, val future: AST?, val past: Trace?)

fun Trace.isFinished() =
    future == null

fun Trace.next() = when (future) {
    null -> this
    else -> future.reduce().let { (action, future) -> Trace(action, future, this) }
}

fun Trace.run(): Trace {
    var current: Trace = this
    while (!current.isFinished()) current = current.next()
    return current
}
