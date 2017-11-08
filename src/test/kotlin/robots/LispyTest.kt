package robots

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class LispyTest {
    @Test
    fun `displays AST in a syntax inspired by, but not the same as, LISP`() {
        assertThat(Seq(Action("a"), Seq(Action("b"), Action("c")), Repeat(4, Action("d"))).toCompactString(),
            equalTo("[a; [b; c]; 4 * d]"))
    }
}