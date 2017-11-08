package robots

data class Reduction(val action: Action?, val future: AST?)

fun AST.reduce(): Reduction =
    when (this) {
        is Action -> Reduction(this, null)
        is Repeat -> {
            if (times == 1) {
                repeated.reduce()
            }
            else if (times > 1) {
                val headReduction = repeated.reduce()
                val remaining = Repeat(times - 1, repeated)
                headReduction.copy(future = if (headReduction.future == null) remaining else Seq(headReduction.future, remaining))
                
            }
            else {
                throw IllegalStateException("cannot reduce $this")
            }
        }
        is Seq ->
            when (steps) {
                Empty ->
                    Reduction(null, null)
                is Cons<AST> -> {
                    val headReduction = steps.head.reduce()
                    headReduction.copy(future = if (headReduction.future == null) Seq(steps.tail) else Seq(Cons(headReduction.future, steps.tail)))
                }
            }
    }

