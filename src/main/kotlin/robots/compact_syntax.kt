package robots

import com.natpryce.Err
import com.natpryce.Ok
import com.natpryce.Result
import org.javafp.data.IList
import org.javafp.parsecj.Combinators
import org.javafp.parsecj.Message
import org.javafp.parsecj.Parser
import org.javafp.parsecj.Reply
import org.javafp.parsecj.Text
import org.javafp.parsecj.input.Input

fun AST.toCompactString(): String = when (this) {
    is Action -> name
    is Repeat -> times.toString() + "×" + repeated.toCompactString()
    is Seq -> "[" + steps.toCompactString() + "]"
}

private fun PList<AST>.toCompactString(): String {
    return when (this) {
        is Cons<AST> -> head.toCompactString() + (if (tail == Empty) "" else ", ") + tail.toCompactString()
        Empty -> ""
    }
}


private val action: Parser<Char, AST> = Text.alpha.map { start: Char -> Action(start.toString()) }

private val repeat: Parser<Char, AST> =
    Text.intr.bind { count -> Text.string("×").then(seqParser).map<AST> { seq -> Repeat(count.toInt(), seq) } }

private val seqElement: Parser<Char, AST> = Combinators.choice(action, repeat, recursive { seqParser })

private val seqParser: Parser<Char, Seq> =
    seqElement.between(Text.wspaces, Text.wspaces).sepBy(Text.string(",")).between(Text.string("["), Text.string("]"))
        .map { actions -> Seq(actions.toPList()) }

private val topLevel: Parser<Char, Seq> = Text.wspaces.then(seqParser).followedBy(Text.wspaces.then(Combinators.eof()))

private fun <I, A, B> Parser<I, A>.followedBy(after: Parser<I, B>) =
    bind { a -> after.then(Combinators.retn(a)) }

private fun <I, A> recursive(supplier: () -> Parser<I, A>): Parser<I, A> {
    val parser = lazy(supplier)
    return Parser<I, A> { input -> parser.value.apply(input) }
}

private fun <T> IList<T>.toPList(): PList<T> =
    if (this.isEmpty) Empty else Cons(this.head(), this.tail().toPList())

fun String.toSeq(): Result<Seq, Message<Char>> {
    return topLevel.parse(Input.of(this)).asResult()
}

private fun <I, A> Reply<I, A>.asResult() = if (isOk) Ok(result) else Err(msg)

