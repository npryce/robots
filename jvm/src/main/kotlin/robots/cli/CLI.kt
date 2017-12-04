@file:JvmName("CLI")
package robots.cli

import com.natpryce.onError
import robots.Seq
import robots.Trace
import robots.next
import robots.reduce
import robots.reduceToAction
import robots.start
import robots.toCompactString
import robots.toSeq

fun main(args: Array<String>) {
    var original = Trace(null, null, null)
    var history = original
    
    generateSequence { readCommand { history } }
        .forEach { line ->
            val parts = line.split(' ', limit = 2)
            
            when (parts.getOrNull(0)) {
                "l" -> {
                    original = start(
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
                    history = original
                }
                "r" ->
                    history = history.next(Seq::reduce)
                "R" ->
                    history = history.next(Seq::reduceToAction)
                "b" ->
                    history = history.past ?: history
                "B" ->
                    history = original
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
        println("(" + (trace.action?.text ?: " ") + ") " + (trace.next?.toCompactString() ?: ""))
    }
    
    System.out.print("> ")
    System.out.flush()
    return readLine()
}
