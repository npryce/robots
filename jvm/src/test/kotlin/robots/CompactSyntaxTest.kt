package robots

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters


@RunWith(Parameterized::class)
class CompactSyntaxTest(val what: String, val syntax: String, val ast: Seq) {
    companion object {
        private val a = Action("a")
        private val b = Action("b")
        private val c = Action("c")
        private val d = Action("d")
        private val e = Action("e")
    
        private val examples: Map<String, Pair<String, Seq>> = mapOf(
            "empty program" to Pair("", nop),
            "sequence of single action" to Pair("a", Seq(a)),
            "sequence of actions" to Pair("a, b, c", Seq(a, b, c)),
            "nested program" to Pair("a, [b, c], 4•[d, e]", Seq(a, Seq(b, c), Repeat(4, d, e))),
            "funky identifiers" to Pair("3•[⬆], 💩", Seq(Repeat(3, Action("⬆")), Action("💩")))
        )
        
        @JvmStatic
        @Parameters(name = "{0}: {1}")
        fun params() =
            examples.map { (name, value) -> arrayOf(name, value.first, value.second) }
    }
    
    @Test
    fun `parses`() {
        assertThat(what, syntax.toSeq().ok(), equalTo(ast))
    }
    
    @Test
    fun `formats`() {
        assertThat(what, ast.toCompactString(), equalTo(syntax))
    }
}