package robots


fun Seq.cost(): Int =
    steps.cost(0)

private tailrec fun  PList<AST>.cost(acc: Int): Int = when (this) {
    Empty -> acc
    is Cons<AST> -> tail.cost(head.cost(acc))
}

private fun AST.cost(acc: Int): Int = when(this) {
    is Action -> acc + 1
    is Repeat -> repeated.cost(acc + 1)
    is Seq -> steps.cost(acc)
}
