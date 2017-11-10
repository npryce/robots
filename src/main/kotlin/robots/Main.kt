package robots

fun main(args: Array<String>) {
    val ast = Seq(Repeat(2, Seq(Repeat(4, Seq(Action("clockwise"))), Action("forward"))))
    
    val trace = start(ast).run(Seq::reduce)
    
    report(trace)
}

private tailrec fun report(trace: Trace) {
    println("(" + (trace.action?.name ?: " ") + ") " + (trace.future?.toCompactString() ?: ""))
    if (trace.past != null) {
        report(trace.past)
    }
}
