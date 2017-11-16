package robots

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class ParseCompactStringTest {
    @Test
    fun `parse empty seq`() {
        assertThat("[]".toSeq().ok(),
            equalTo(nop))
    }
    
    @Test
    fun `parse sequence of single action`() {
        assertThat("[a]".toSeq().ok(),
            equalTo(Seq(Action("a"))))
    }
    
    @Test
    fun `parse sequence of actions`() {
        assertThat("[a, b, c]".toSeq().ok(),
            equalTo(Seq(a, b, c)))
    }
    
    @Test
    fun `parse compact string`() {
        assertThat("[a, [b, c], 4•[d, e]]".toSeq().ok(),
            equalTo(Seq(a, Seq(b, c), Repeat(4, Seq(d, e)))))
    }
}