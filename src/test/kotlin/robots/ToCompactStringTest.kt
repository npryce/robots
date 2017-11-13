package robots

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.javafp.data.IList
import org.javafp.parsecj.Combinators
import org.javafp.parsecj.Combinators.eof
import org.javafp.parsecj.Combinators.retn
import org.javafp.parsecj.ConsumedT
import org.javafp.parsecj.Parser
import org.javafp.parsecj.Text.alpha
import org.javafp.parsecj.Text.intr
import org.javafp.parsecj.Text.string
import org.javafp.parsecj.Text.wspaces
import org.javafp.parsecj.input.Input
import org.junit.Test

class ToCompactStringTest {
    @Test
    fun `displays AST on one line with a compact but readable syntax`() {
        assertThat(Seq(Action("a"), Seq(Action("b"), Action("c")), Repeat(4, Seq(Action("d"), Action("e")))).toCompactString(),
            equalTo("[a, [b, c], 4×[d, e]]"))
    }
}

class ParseCompactStringTest {
    @Test
    fun `parse empty seq`() {
        assertThat("[]".toSeq(),
            equalTo(nop))
    }
    
    @Test
    fun `parse sequence of single action`() {
        assertThat("[a]".toSeq(),
            equalTo(Seq(Action("a"))))
    }
    
    @Test
    fun `parse sequence of actions`() {
        assertThat("[a, b, c]".toSeq(),
            equalTo(Seq(Action("a"), Action("b"), Action("c"))))
    }
    
    @Test
    fun `parse compact string`() {
        assertThat("[a, [b, c], 4×[d, e]]".toSeq(),
            equalTo(Seq(Action("a"), Seq(Action("b"), Action("c")), Repeat(4, Seq(Action("d"), Action("e"))))))
    }
    
}

val action: Parser<Char, AST> = alpha.map { start: Char -> Action(start.toString()) }

val repeat: Parser<Char, AST> =
    intr.bind { count -> string("×").then(seqParser).map<AST> { seq -> Repeat(count.toInt(), seq) } }

val seqElement: Parser<Char, AST> = Combinators.choice(action, repeat, recursive { seqParser })

val seqParser: Parser<Char, Seq> =
    seqElement.between(wspaces, wspaces).sepBy(string(",")).between(string("["), string("]"))
        .map { actions -> Seq(actions.toPList()) }

val topLevel: Parser<Char, Seq> = wspaces.then(seqParser).followedBy(wspaces.then(eof()))

private fun <I, A, B> Parser<I, A>.followedBy(after: Parser<I, B>) =
    bind { a -> after.then(retn(a)) }

private fun <I, A> recursive(supplier: () -> Parser<I, A>): Parser<I, A> {
    val parser = lazy(supplier)
    return Parser<I, A> { input -> parser.value.apply(input) }
}

private fun <T> IList<T>.toPList(): PList<T> =
    if (this.isEmpty) Empty else Cons(this.head(), this.tail().toPList())

fun String.toSeq(): Seq {
    return topLevel.parse(Input.of(this)).result
}

