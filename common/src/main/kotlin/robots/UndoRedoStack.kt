package robots


data class UndoRedoStack<T>(
    private val undoStack: PList<T> = emptyPList(),
    private val redoStack: PList<T> = emptyPList()) {
    
    fun havingDone(state: T) =
        copy(undoStack = Cons(state, undoStack), redoStack = emptyPList())
    
    fun undo() =
        undoStack.notEmpty { (head, tail) -> copy(undoStack = tail, redoStack = Cons(head, redoStack)) } ?: this
    
    fun redo() =
        redoStack.notEmpty { (head, tail) -> copy(undoStack = Cons(head, undoStack), redoStack = tail) } ?: this
}

