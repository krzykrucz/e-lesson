package com.krzykrucz.elesson.currentlesson

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.getOrHandle
import arrow.effects.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.domain.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartLessonError
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

data class MonoDomainError(val domainError: Any) : RuntimeException()

fun Mono<ServerResponse>.handleErrors(): Mono<ServerResponse> = this.onErrorResume {
    when (it) {
        is MonoDomainError -> {
            val status: HttpStatus = when (it.domainError) {
                is StartLessonError.ClassRegistryUnavailable -> HttpStatus.INTERNAL_SERVER_ERROR
                else -> HttpStatus.BAD_REQUEST
            }
            ServerResponse.status(status)
                .body(BodyInserters.fromObject(it.domainError.javaClass.simpleName))
        }
        else -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .render(it.localizedMessage)
    }
}

fun <S, E> Mono<AsyncOutput<E, S>>.flattenAsyncOutput() = this.flatMap { asyncOutput ->
    asyncOutput.map { output -> output.map { Mono.just(it) } }
        .map { output -> output.getOrHandle { error -> Mono.error(MonoDomainError(error as Any)) } }
        .unsafeRunTimed(Duration(3, TimeUnit.SECONDS))
        .getOrElse { Mono.error(RuntimeException()) }
}

fun <A, B> Either<A, B>.toServerResponse(): Mono<ServerResponse> =
    this.fold(
        ifLeft = { ServerResponse.badRequest().body(BodyInserters.fromObject(it)) },
        ifRight = { ServerResponse.ok().body(BodyInserters.fromObject(it)) }
    )