package robots

import com.natpryce.onError

fun main(args: Array<String>) {
    var history = Trace(null, null, null)
    
    generateSequence { readCommand { history } }
        .forEach { line ->
            val parts = line.split(' ', limit = 2)
            
            when (parts.getOrNull(0)) {
                "l" ->
                    history = start(
                        if (parts.size == 1) {
                            history.toSeq()
                        }
                        else {
                            parts[1].toSeq()
                                .onError {
                                    System.err.println(it.reason)
                                    return@forEach
                                }
                        })
                "r" ->
                    history = history.next(Seq::reduce)
                "R" ->
                    history = history.next(Seq::reduceToAction)
                "b" ->
                    history = history.past ?: history
                else ->
                    System.err.println("unknown command: $line")
            }
        }
}

private fun readCommand(latest: () -> Trace): String? {
    System.err.flush()
    System.out.flush()
    
    val trace = latest()
    if (trace.action != null || trace.next != null) {
        println("(" + (trace.action?.name ?: " ") + ") " + (trace.next?.toCompactString() ?: ""))
    }
    
    System.out.print("> ")
    System.out.flush()
    return readLine()
}

private tailrec fun report(trace: Trace) {
    println("(" + (trace.action?.name ?: " ") + ") " + (trace.next?.toCompactString() ?: ""))
    if (trace.past != null) {
        report(trace.past)
    }
}
