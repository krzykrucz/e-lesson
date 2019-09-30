package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.Either
import arrow.core.Predicate
import arrow.core.flatMap
import arrow.core.getOrHandle
import arrow.data.extensions.list.traverse.traverse
import arrow.data.fix
import arrow.effects.IO
import arrow.effects.extensions.io.applicative.applicative
import arrow.effects.fix

data class NonEmptyText(val text: String) {
    companion object {
        fun of(string: String): NonEmptyText? =
                if (string.isNotEmpty()) NonEmptyText(string)
                else null
    }
}

data class WholeNumber private constructor(val number: Int) {
    operator fun minus(number: WholeNumber): WholeNumber? = of(this.number - number.number)

    companion object {
        val ZERO = WholeNumber(0)
        val ONE = WholeNumber(1)
        val TWO = WholeNumber(2)
        val THREE = WholeNumber(3)
        fun of(int: Int): WholeNumber? =
                if (int >= 0) WholeNumber(int)
                else null
    }
}

data class NaturalNumber private constructor(val number: Int) {
    companion object {
        val ONE = NaturalNumber(1)
        val TWO = NaturalNumber(2)
        val THREE = NaturalNumber(3)
        fun of(int: Int): NaturalNumber? =
                if (int > 0) NaturalNumber(int)
                else null
    }
}

data class NonNegativeRealNumber private constructor(val number: Double) {
    operator fun minus(number: NonNegativeRealNumber): NonNegativeRealNumber? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val ZERO: NonNegativeRealNumber = NonNegativeRealNumber(0.0)

        fun of(double: Double): NonNegativeRealNumber? =
                if (double >= 0) NonNegativeRealNumber(double)
                else null
    }
}

class NonEmptySet<T> private constructor(private val elements: Set<T>) : Set<T> by elements {
    companion object {
        fun <T> create(element: T): NonEmptySet<T> = NonEmptySet(setOf(element))

        fun <T> create(first: T, vararg rest: T): NonEmptySet<T> = NonEmptySet(setOf(first) + rest)

        fun <T> create(set: Set<T>): NonEmptySet<T>? = NonEmptySet(set)
    }

}

typealias Output<Success, Error> = Either<Error, Success>
typealias AsyncOutput<Success, Error> = IO<Output<Success, Error>>
typealias Async<T> = IO<T>


fun <A, B> List<A>.mapAsync(function: (A) -> Async<B>): Async<List<B>> =
        this.traverse(IO.applicative()) { a -> function(a) }.fix()
                .map { it.fix().toList() }


fun <Success, Error> AsyncOutput<Success, Error>.failIf(predicate: Predicate<Success>, error: Error): AsyncOutput<Success, Error> {
    return this.map { either -> either.flatMap { success: Success -> if (predicate(success)) Either.Left(error) else Either.Right(success) } }
}


fun <S1, Error, S2> AsyncOutput<S1, Error>.mapSuccess(transformer: (S1) -> S2): AsyncOutput<S2, Error> {
    return this.map { either -> either.map(transformer) }
}

fun <Success, E1, E2> AsyncOutput<Success, E1>.mapError(transformer: (E1) -> E2): AsyncOutput<Success, E2> {
    return this.map { either -> either.mapLeft(transformer) }
}

fun <S1, Error, S2> AsyncOutput<S1, Error>.flatMapSuccess(transformer: (S1) -> AsyncOutput<S2, Error>): AsyncOutput<S2, Error> {
    return this.flatMap { either ->
        either.map { transformer(it) }
                .getOrHandle { IO.just(Either.left(it)) }
    }
}

fun <T, E> Output<T, E>.isSuccess() = this.isRight()
fun <T, E> Output<T, E>.isError() = this.isLeft()

class AsyncFactory {
    companion object {
        fun <S> justSuccess(success: S) = IO.just(Either.right(success))
        fun <E> justError(error: E) = IO.just(Either.left(error))
    }
}
