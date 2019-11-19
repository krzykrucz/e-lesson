package com.krzykrucz.elesson.currentlesson.domain.shared

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Predicate
import arrow.core.Some
import arrow.core.extensions.either.applicativeError.handleError
import arrow.core.flatMap
import arrow.core.getOrHandle
import arrow.core.right
import arrow.core.maybe
import arrow.core.right
import arrow.effects.IO

data class NonEmptyText(val text: String) {
    companion object {
        fun of(string: String): NonEmptyText? =
                if (string.isNotEmpty()) NonEmptyText(string)
                else null
    }
}

data class WholeNumber private constructor(val number: Int) {
    operator fun minus(number: WholeNumber): WholeNumber? =
        of(this.number - number.number)
    operator fun inc(): WholeNumber = this.copy(number = this.number + 1)
    operator fun compareTo(number: Int): Int = this.number.compareTo(number)

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
        val FOUR = NaturalNumber(4)
        val FIVE = NaturalNumber(5)
        val SIX = NaturalNumber(6)
        val SEVEN = NaturalNumber(7)
        val EIGHT = NaturalNumber(8)
        fun of(int: Int): Option<NaturalNumber> =
                if (int > 0) Some(NaturalNumber(int))
                else None
    }
    operator fun plus(that: NaturalNumber): NaturalNumber =
        NaturalNumber(this.number + that.number)
}

data class Digit internal constructor(val digit: Int) {

    operator fun compareTo(other: Digit) = digit.compareTo(other = other.digit)

    companion object {
        val ZERO = Digit(0)
        val ONE = Digit(1)
        val TWO = Digit(2)
        val THREE = Digit(3)
        val FOUR = Digit(4)
        val FIVE = Digit(5)
        val SIX = Digit(6)
        val SEVEN = Digit(7)
        val EIGHT = Digit(8)
        val NINE = Digit(9)

        fun of(int: Int): Option<Digit> = (int >= 0).maybe { Digit(int) }
    }

    override fun toString() = "$digit"
}


data class NonNegativeRealNumber private constructor(val number: Double) {
    operator fun minus(number: NonNegativeRealNumber): NonNegativeRealNumber? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val ZERO: NonNegativeRealNumber =
            NonNegativeRealNumber(0.0)

        fun of(double: Double): NonNegativeRealNumber? =
                if (double >= 0) NonNegativeRealNumber(double)
                else null
    }
}

class NonEmptySet<T> private constructor(private val elements: Set<T>) : Set<T> by elements {
    companion object {
        fun <T> create(element: T): NonEmptySet<T> =
            NonEmptySet(setOf(element))

        fun <T> create(first: T, vararg rest: T): NonEmptySet<T> =
            NonEmptySet(setOf(first) + rest)

        fun <T> create(set: Set<T>): NonEmptySet<T>? =
            NonEmptySet(set)
    }
}

class NonEmptyList<T> private constructor(private val elements: List<T>) : List<T> by elements {
    companion object {
        fun <T> create(element: T): NonEmptyList<T> =
            NonEmptyList(listOf(element))

        fun <T> create(first: T, vararg rest: T): NonEmptyList<T> =
            NonEmptyList(listOf(first) + rest)

        fun <T> create(list: List<T>): Option<NonEmptyList<T>> =
                if (list.isEmpty()) Option.empty()
                else Option.just(NonEmptyList(list))
    }
}

typealias Async<T> = IO<T>
typealias Output<Error, Success> = Either<Error, Success>
typealias AsyncOutput<Error, Success> = IO<Output<Error, Success>>
typealias AsyncOutputFactory = IO.Companion


fun <Success, Error> AsyncOutput<Error, Success>.failIf(predicate: Predicate<Success>, error: Error): AsyncOutput<Error, Success> {
    return this.map { either -> either.flatMap { success: Success -> if (predicate(success)) Either.Left(error) else Either.Right(success) } }
}

fun <Success, Error> AsyncOutput<Error, Success>.handleError(handler: (Error) -> Success): AsyncOutput<Error, Success> {
    return this.map { either -> either.handleError(handler) }
}


fun <S1, Error, S2> AsyncOutput<Error, S1>.mapSuccess(transformer: (S1) -> S2): AsyncOutput<Error, S2> {
    return this.map { either -> either.map(transformer) }
}

fun <Success, E1, E2> AsyncOutput<E1, Success>.mapError(transformer: (E1) -> E2): AsyncOutput<E2, Success> {
    return this.map { either -> either.mapLeft(transformer) }
}

fun <S1, Error, S2> AsyncOutput<Error, S1>.flatMapAsyncSuccess(transformer: (S1) -> AsyncOutput<Error, S2>): AsyncOutput<Error, S2> {
    return this.flatMap { either ->
        either.map { transformer(it) }
                .getOrHandle { IO.just(Either.left(it)) }
    }
}

fun <S1, Error, S2> AsyncOutput<Error, S1>.flatMapSuccess(transformer: (S1) -> Output<Error, S2>): AsyncOutput<Error, S2> {
    return this.flatMap { either ->
        either.map { success -> this.map { transformer(success) } }
                .getOrHandle { IO.just(Either.left(it)) }
    }
}

fun <T, E> Output<E, T>.isSuccess() = this.isRight()
fun <T, E> Output<E, T>.isError() = this.isLeft()

class AsyncFactory {
    companion object {
        fun <S> justSuccess(success: S) = IO.just(Either.right(success))
        fun <E> justError(error: E) = IO.just(Either.left(error))
    }
}
