package robots

data class Reduction(val action: Action?, val future: AST?)

tailrec fun AST.reduceToAction(): Reduction {
    val r = reduce()
    return if (r.action != null || r.future == null) r else r.future.reduceToAction()
}

fun AST.reduce(): Reduction =
    when (this) {
        is Action -> Reduction(this, null)
        is Repeat -> {
            if (times == 1) {
                Reduction(null, repeated)
            }
            else if (times > 1) {
                Reduction(null, Seq(repeated, Repeat(times-1, repeated)))
            }
            else {
                throw IllegalStateException("cannot reduce $this")
            }
        }
        is Seq ->
            reduce()
    }

private fun Seq.reduce(): Reduction {
    return when (steps) {
        Empty ->
            Reduction(null, null)
        is Cons<AST> -> {
            val headReduction = steps.head.reduce()
            headReduction.copy(future = if (headReduction.future == null) Seq(steps.tail) else Seq(Cons(headReduction.future, steps.tail)))
        }
    }
}

