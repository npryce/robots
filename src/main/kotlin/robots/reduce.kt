package robots

data class Reduction(val action: Action?, val future: Seq?)

tailrec fun Seq.reduceToAction(): Reduction {
    val r = reduce()
    return if (r.action != null || r.future == null) r else r.future.reduceToAction()
}

fun Seq.reduce(): Reduction =
    when (steps) {
        Empty -> Reduction(null, null)
        is Cons<AST> -> reduceCons(steps.head, steps.tail)
    }

private fun reduceCons(head: AST, tail: PList<AST>) = when (head) {
    is Action ->
        Reduction(head, Seq(tail))
    is Repeat ->
        Reduction(null,
            if (head.times > 1) {
                Seq(Cons(head.repeated, Cons(head.copy(times = head.times - 1), tail)))
            }
            else if (head.times == 1) {
                Seq(Cons(head.repeated, tail))
            }
            else {
                Seq(tail)
            })
    is Seq ->
        Reduction(null,
            when (head.steps) {
                Empty -> Seq(tail)
                is Cons<AST> -> Seq(Cons(head.steps.head, Cons(Seq(head.steps.tail), tail)))
            })
}
