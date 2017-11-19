package robots


interface EditPoint {
    fun typeId(): String
    fun remove(): Seq
    fun replaceWith(newAST: AST): Seq
    fun insertBefore(newAST: AST): Seq
}


