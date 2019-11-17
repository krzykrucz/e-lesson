package com.krzykrucz.elesson.currentlesson.shared

import arrow.core.Either
import arrow.core.extensions.either.traverse.sequence
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix

fun <A, B> Either<A, IO<Either<A, B>>>.sequence(): IO<Either<A, Either<A, B>>> =
    this.sequence(IO.applicative()).fix().map { it.fix() }
