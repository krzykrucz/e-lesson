package com.krzykrucz.elesson.currentlesson.domain.shared

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right

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

fun <L, R> Either<L, R>.failIf(predicate: (R) -> Boolean, defaultLeft: L): Either<L, R> =
    flatMap { if (predicate(it)) defaultLeft.left() else it.right() }

fun <T, E> Either<E, T>.isSuccess() = this.isRight()
fun <T, E> Either<E, T>.isError() = this.isLeft()
