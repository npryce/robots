package robots


typealias UndoRedoStack<T> = PListFocus<T>

fun <T> UndoRedoStack(initialState: T) =
    PListFocus(Empty, initialState, Empty)

fun <T> UndoRedoStack<T>.havingDone(nextState: T) =
    copy(Cons(current, back), nextState, Empty)

fun <T> UndoRedoStack<T>.canUndo() = hasPrev()
fun <T> UndoRedoStack<T>.undo() = prev() ?: this

fun <T> UndoRedoStack<T>.canRedo() = hasNext()
fun <T> UndoRedoStack<T>.redo() = next() ?: this
