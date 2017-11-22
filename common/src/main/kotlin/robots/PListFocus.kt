package robots

data class PListFocus<T>(private val back: PList<T>, val current: T, private val forth: PList<T>) {
    fun hasNext(): Boolean =
        forth.isNotEmpty()
    
    fun next(): PListFocus<T>? =
        forth.notEmpty { (head, tail) -> PListFocus(Cons(current, back), head, tail) }
    
    fun hasPrev(): Boolean =
        back.isNotEmpty()
    
    fun prev(): PListFocus<T>? =
        back.notEmpty { (head, tail) -> PListFocus(tail, head, Cons(current, forth)) }
    
    fun remove(): PListFocus<T>? =
        forth.notEmpty { (head, tail) -> PListFocus(back, head, tail) }
            ?: back.notEmpty { (head, tail) -> PListFocus(tail, head, forth) }
    
    fun insertBefore(t: T): PListFocus<T> = PListFocus(back, t, Cons(current, forth))
    
    fun insertAfter(t: T): PListFocus<T> = PListFocus(back, current, Cons(t, forth))
    
    fun replaceWith(t: T): PListFocus<T> = copy(current = t)
    
    fun toPList(): PList<T> = back.fold(Cons(current, forth), { xs, x -> Cons(x, xs) })
}

fun <T> PList<T>.zipper(): PListFocus<T>? =
    this.notEmpty { (head, tail) -> PListFocus(Empty, head, tail) }
