package robots

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class ToCompactStringTest {
    @Test
    fun `displays AST in a syntax inspired by, but not the same as, LISP`() {
        assertThat(Seq(Action("a"), Seq(Action("b"), Action("c")), Repeat(4, Seq(Action("d"), Action("e")))).toCompactString(),
            equalTo("[a, [b, c], 4×[d, e]]"))
    }
}