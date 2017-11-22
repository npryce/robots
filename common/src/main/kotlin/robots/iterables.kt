package robots


fun <T> Iterable<T>.splitAfter(isChunkEnd: (T)-> Boolean): List<List<T>> {
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
