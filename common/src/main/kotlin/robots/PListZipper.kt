package robots

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