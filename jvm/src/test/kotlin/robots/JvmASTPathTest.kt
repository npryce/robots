package robots

import org.junit.Test
import kotlin.test.assertEquals

class JvmASTPathTest : ASTPathTest() {
    @Test
    fun `will run on the JVM`() {
    }
    
    override fun assertEquals(actual: Seq, expected: Seq) {
        assertEquals(actual = actual.toCompactString(), expected = expected.toCompactString())
    }
}