package robots

data class Reduction(val prev: Seq, val action: Action?, val next: Seq)

tailrec fun Seq.reduceUntil(p: (Reduction)->Boolean): Reduction {
    val r = reduce()
    return if (p(r) || r.next == nop) r else r.next.reduceUntil(p)
}

fun Seq.reduceToAction() =
    reduceUntil { it.action != null }

fun Seq.reduce(): Reduction =
    when (steps) {
        Empty -> Reduction(this, null, this)
        is Cons<AST> -> reduceHead(steps.head, steps.tail)
    }

private fun Seq.reduceHead(head: AST, tail: PList<AST>) = when (head) {
    is Action ->
        Reduction(this, head, Seq(tail))
    is Repeat ->
        Reduction(this, null,
            when {
                head.times > 1 ->
                    Seq(Cons(Seq(head.repeated), Cons(head.remainingIterations(), tail)))
                head.times == 1 ->
                    Seq(Cons(Seq(head.repeated), tail))
                else ->
                    Seq(tail)
            })
    is Seq ->
        Reduction(this, null,
            when (head.steps) {
                Empty -> Seq(tail)
                is Cons<AST> -> {
                    Seq(Cons(head.steps.head, if (head.steps.tail.isEmpty()) tail else Cons(Seq(head.steps.tail), tail)))
                }
            })
}

private fun Repeat.remainingIterations() =
    copy(times = times - 1)

fun Reduction.next(stepType: (Seq)->Reduction?) =
    stepType(this.next)
