package com.krzykrucz.elesson.currentlesson.infrastructure

import arrow.core.getOrElse
import arrow.fx.IO
import arrow.fx.typeclasses.Duration
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

fun arrow.effects.IO<Mono<ServerResponse>>.run(): Mono<ServerResponse> =
    this.unsafeRunTimed(arrow.effects.typeclasses.Duration(3, TimeUnit.SECONDS))
        .getOrElse { ServerResponse.badRequest().build() }


fun IO<Mono<ServerResponse>>.run(): Mono<ServerResponse> =
    this.unsafeRunTimed(Duration(3, TimeUnit.SECONDS))
        .getOrElse { ServerResponse.badRequest().build() }
