package robots


sealed class PList<out T> : Iterable<T> {
    abstract val head: T?
    abstract val tail: PList<T>
    
    override fun iterator(): Iterator<T> =
        PListIterator(this)
    
    override fun toString() =
        joinToString(prefix = "[", separator = ", ", postfix = "]")
}

object Empty : PList<Nothing>() {
    override val head get() = null
    override val tail get() = this
    override fun toString() = super.toString()
}

data class Cons<out T>(override val head: T, override val tail: PList<T>) : PList<T>() {
    override fun toString() = super.toString()
}

fun PList<*>.isEmpty() = this == Empty
fun PList<*>.isNotEmpty() = !isEmpty()

fun emptyPList() = Empty
fun <T> pListOf(element: T) = Cons(element, Empty)
fun <T> pListOf(vararg elements: T) = elements.foldRight(Empty, ::Cons)


private class PListIterator<out T>(private var current: PList<T>) : Iterator<T> {
    override fun hasNext() =
        current.isNotEmpty()
    
    override fun next() =
        (current.head ?: throw NoSuchElementException())
            .also { current = current.tail }
}
