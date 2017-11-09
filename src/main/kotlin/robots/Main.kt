package robots

fun main(args: Array<String>) {
    val ast = Repeat(2, Seq(Repeat(4, Action("clockwise")), Action("forward")))
    
    val trace = start(ast).run()
    
    report(trace)
}

private tailrec fun report(trace: Trace) {
    println("(" + (trace.action?.name ?: " ") + ") " + (trace.future?.toCompactString() ?: ""))
    if (trace.past != null) {
        report(trace.past)
    }
}
