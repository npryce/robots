package robots

import com.natpryce.Result
import com.natpryce.onError

internal inline fun <reified T, E> Result<T, E>.ok(): T {
    return onError { throw AssertionError("expected Ok<${T::class.simpleName}>, was ${this}")}
}
