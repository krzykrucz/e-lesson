package com.krzykrucz.elesson.currentlesson.domain.base

import arrow.core.maybe
import arrow.fx.IO
import java.time.LocalTime
import java.time.format.DateTimeFormatter


sealed class Option<T>
object None : Option<Nothing>()
data class Some<T>(val value: T) : Option<T>()

sealed class Either<L, R>
data class Left<L>(val value: L) : Either<L, Nothing>()
data class Right<R>(val value: R) : Either<Nothing, R>()

typealias Result<T, E> = Either<E, T>

typealias AsyncResult<T, E> = IO<Result<T, E>>


typealias Text = String

data class NonEmptyText private constructor(val text: Text) {
    companion object {
        fun create(text: Text) =
            text.isNotEmpty()
                .maybe { NonEmptyText(text) }
    }
}

typealias Time = LocalTime

fun timeOf(string: String) =  Time.parse(string, DateTimeFormatter.ofPattern("hh:mma"))