package robots


fun <T> Iterable<T>.splitAfter(isChunkEnd: (T) -> Boolean): List<List<T>> {
    val chunks = mutableListOf<List<T>>()
    val chunk = mutableListOf<T>()
    
    forEach {
        chunk += it
        if (isChunkEnd(it)) {
            chunks += chunk.toList()
            chunk.clear()
        }
    }
    
    if (chunk.isNotEmpty()) chunks += chunk.toList()
    
    return chunks.toList()
}

fun <T> Sequence<T>.takeUntil(p: (T) -> Boolean): Sequence<T> = object : Sequence<T> {
    override fun iterator(): Iterator<T> = object : Iterator<T> {
        val source = this@takeUntil.iterator()
        var lastWasTerminator = false
        
        override fun hasNext(): Boolean =
            source.hasNext() && !lastWasTerminator
        
        override fun next(): T =
            if (hasNext()) {
                source.next().also { lastWasTerminator = p(it) }
            }
            else {
                throw NoSuchElementException()
            }
    }
}
