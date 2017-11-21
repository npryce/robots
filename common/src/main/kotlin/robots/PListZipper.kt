package robots

data class PListZipper<T>(private val back: PList<T>, val current: T, private val forth: PList<T>) {
    fun next(): PListZipper<T>? = when (forth) {
        Empty -> null
        is Cons<T> -> PListZipper(Cons(current, back), forth.head, forth.tail)
    }
    
    fun prev(): PListZipper<T>? = when (back) {
        Empty -> null
        is Cons<T> -> PListZipper(back.tail, back.head, Cons(current, forth))
    }
    
    fun remove(): PListZipper<T>? = when (forth) {
        is Cons<T> -> PListZipper(back, forth.head, forth.tail)
        Empty -> when (back) {
            is Cons<T> -> PListZipper(back.tail, back.head, forth)
            Empty -> null
        }
    }
    
    fun insert(t: T): PListZipper<T> = PListZipper(back, t, Cons(current, forth))
    
    fun replaceWith(t: T): PListZipper<T> = copy(current = t)
    
    fun toPList(): PList<T> = back.fold(Cons(current, forth), { xs, x -> Cons(x, xs) })
}

fun <T> PList<T>.zipper(): PListZipper<T>? = when (this) {
    Empty -> null
    is Cons<T> -> PListZipper(Empty, head, tail)
}
