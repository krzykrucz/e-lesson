package com.krzykrucz.elesson.currentlesson.adapters

import arrow.core.Either
import arrow.core.EitherOf
import arrow.core.fix
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait


suspend fun <A, B, C> EitherOf<A, B>.asyncFlatMap(f: suspend (B) -> Either<A, C>): Either<A, C> =
    fix().let {
        when (it) {
            is Either.Right -> f(it.b)
            is Either.Left -> it
        }
    }

suspend fun <A, B, C> EitherOf<A, B>.asyncMap(f: suspend (B) -> C): Either<A, C> =
    asyncFlatMap { Either.Right(f(it)) }

suspend fun <A, B> EitherOf<A, B>.asyncDoIfRight(f: suspend (B) -> Unit): Either<A, B> =
    asyncMap {
        f(it)
        it
    }

suspend fun <A, B> Either<A, B>.toServerResponse(): ServerResponse =
    this.fold(
        ifLeft = { ServerResponse.badRequest().bodyValueAndAwait(it as Any) },
        ifRight = { ServerResponse.ok().bodyValueAndAwait(it as Any) }
    )
