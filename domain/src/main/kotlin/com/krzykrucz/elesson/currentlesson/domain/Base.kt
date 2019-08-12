package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.Either
import arrow.effects.IO

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

typealias Output<T> = Either<LessonError, T>
typealias AsyncOutput<T> = IO<Output<T>>

typealias Output2<T, E> = Either<E, T>
typealias AsyncOutput2<T, E> = IO<Output2<T, E>>

fun <T> asyncSuscess(t: T): AsyncOutput<T> = IO.just(Either.right(t))
fun <T> asyncDomainError(error: LessonError): AsyncOutput<T> = IO.just(Either.left(error))

