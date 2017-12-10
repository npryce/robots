package robots



fun AST.isRunnable(): Boolean = when(this) {
    is Action -> true
    is Seq -> steps.isNotEmpty() && steps.all { it.isRunnable() }
    is Repeat -> times > 0 && repeated.isNotEmpty() && repeated.all { it.isRunnable() }
}
