package com.krzykrucz.elesson.currentlesson.adapters

import arrow.core.Either
import arrow.core.EitherOf
import arrow.core.extensions.either.traverse.sequence
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix

fun <A, B> Either<A, IO<B>>.sequence(): IO<Either<A, B>> =
    this.sequence(IO.applicative()).fix()
        .map { it.fix() }


suspend fun <A, B, C> EitherOf<A, B>.asyncFlatMap(f: suspend (B) -> Either<A, C>): Either<A, C> =
    fix().let {
        when (it) {
            is Either.Right -> f(it.b)
            is Either.Left -> it
        }
    }

suspend fun <A, B, C> EitherOf<A, B>.asyncMap(f: suspend (B) -> C): Either<A, C> =
    asyncFlatMap { Either.Right(f(it)) }

suspend fun <A, B> EitherOf<A, B>.doIfRight(f: suspend (B) -> Unit): Either<A, B> =
    asyncMap {
        f(it)
        it
    }
