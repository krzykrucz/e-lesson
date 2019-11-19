package com.krzykrucz.elesson.currentlesson.adapters

import arrow.core.Either
import arrow.core.extensions.either.traverse.sequence
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix

fun <A, B> Either<A, IO<B>>.sequence(): IO<Either<A, B>> =
    this.sequence(IO.applicative()).fix()
        .map { it.fix() }