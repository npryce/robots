package robots


sealed class PList<out T> {
    abstract val head: T?
    abstract val tail: PList<T>
    abstract fun isEmpty(): Boolean
}
object Empty : PList<Nothing>() {
    override val head get() = null
    override val tail get() = this
    override fun isEmpty() = true
}
data class Cons<out T>(override val head: T, override val tail: PList<T>) : PList<T>() {
    override fun isEmpty() = false
}

fun emptyPList() = Empty
fun <T> pListOf(element: T) = Cons(element, Empty)
fun <T> pListOf(vararg elements: T) = elements.foldRight(Empty, ::Cons)
