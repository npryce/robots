package robots


sealed class PList<out T> : Iterable<T> {
    abstract val head: T?
    abstract val tail: PList<T>
    
    override fun iterator(): Iterator<T> {
        return PListIterator(this)
    }
}

object Empty : PList<Nothing>() {
    override val head get() = null
    override val tail get() = this
    override fun toString() = "Empty"
}

data class Cons<out T>(override val head: T, override val tail: PList<T>) : PList<T>()

fun PList<*>.isEmpty() = this == Empty
fun PList<*>.isNotEmpty() = !isEmpty()

fun emptyPList() = Empty
fun <T> pListOf(element: T) = Cons(element, Empty)
fun <T> pListOf(vararg elements: T) = elements.foldRight(Empty, ::Cons)


class PListIterator<T>(private var current: PList<T>) : Iterator<T> {
    override fun hasNext() =
        current.isNotEmpty()
    
    override fun next() =
        (current.head ?: throw NoSuchElementException())
            .also { current = current.tail }
}


class PListZipper<T>(private val back: PList<T>, private val forth: PList<T>) {
    val current: T? get() = forth.head
    
    fun next(): PListZipper<T>? = forth.head?.let { PListZipper(Cons(it, back), forth.tail) }
    fun prev(): PListZipper<T>? = back.head?.let { PListZipper(back.tail, Cons(it, forth)) }
    fun remove(): PListZipper<T> = PListZipper(back, forth.tail)
    fun insert(t: T): PListZipper<T> = PListZipper(back, Cons(t, forth))
    fun replaceWith(t: T): PListZipper<T> = remove().insert(t)
    
    fun toPList(): PList<T> = back.fold(forth, { xs, x -> Cons(x, xs) })
}

fun <T> PList<T>.zipper() = PListZipper(Empty, this)
