package robots

import com.natpryce.Err
import com.natpryce.Ok
import com.natpryce.Result
import org.javafp.data.IList
import org.javafp.parsecj.Combinators
import org.javafp.parsecj.Combinators.choice
import org.javafp.parsecj.Message
import org.javafp.parsecj.Parser
import org.javafp.parsecj.Reply
import org.javafp.parsecj.Text.intr
import org.javafp.parsecj.Text.regex
import org.javafp.parsecj.Text.string
import org.javafp.parsecj.Text.wspaces
import org.javafp.parsecj.input.Input

private val begin = "["
private val end = "]"
private val multiply = "•"
private val separator = ","

fun AST.toCompactString(): String = when (this) {
    is Action -> name
    is Repeat -> times.toString() + multiply + begin + repeated.toCompactString() + end
    is Seq -> begin + steps.toCompactString() + end
}

private fun PList<AST>.toCompactString(): String {
    return when (this) {
        is Cons<AST> -> head.toCompactString() + (if (tail == Empty) "" else "$separator ") + tail.toCompactString()
        Empty -> ""
    }
}


private val action: Parser<Char, AST> = regex("[^\\$begin\\$end\\$multiply\\$separator\\p{Space}\\p{Digit}]").map(::Action)

private val repeat: Parser<Char, AST> =
    intr.bind { count -> string(multiply).then(sequenceElements).map<AST> { repeated -> Repeat(count.toInt(), repeated) } }

private val sequence: Parser<Char, Seq> =
    recursive { sequenceElements }.map(::Seq).label("sequence")

private val sequenceElement: Parser<Char, AST> = choice(action, repeat, sequence)

private val sequenceElements: Parser<Char, PList<AST>> =
    sequenceElement.between(wspaces, wspaces).sepBy(string(separator)).between(string(begin), string(end))
        .map { elements -> elements.toPList() }

private val topLevel: Parser<Char, Seq> = wspaces.then(sequence).followedBy(wspaces.then(Combinators.eof()))

private fun <I, A, B> Parser<I, A>.followedBy(after: Parser<I, B>) =
    bind { a -> after.then(Combinators.retn(a)) }

private fun <I, A> recursive(supplier: () -> Parser<I, A>): Parser<I, A> {
    val parser = lazy(supplier)
    return Parser { input -> parser.value.apply(input) }
}

private fun <T> IList<T>.toPList(): PList<T> =
    if (this.isEmpty) Empty else Cons(this.head(), this.tail().toPList())

fun String.toSeq(): Result<Seq, Message<Char>> {
    return topLevel.parse(Input.of(this)).asResult()
}

private fun <I, A> Reply<I, A>.asResult() = if (isOk) Ok(result) else Err(msg)

