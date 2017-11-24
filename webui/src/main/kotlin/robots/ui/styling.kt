package robots.ui

import robots.AST
import robots.Action
import robots.Repeat
import robots.Seq

fun cardCategoryClass(instruction: AST) =
    when (instruction) {
        is Action -> "action"
        is Repeat -> "control"
        is Seq -> "control"
    }

fun AST.displayId() =
    when (this) {
        is Action -> name
        is Repeat -> "${times}×"
        is Seq -> "[]"
    }
