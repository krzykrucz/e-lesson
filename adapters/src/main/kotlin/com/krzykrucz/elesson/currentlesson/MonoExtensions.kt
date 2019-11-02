package com.krzykrucz.elesson.currentlesson

import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartLessonError
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

data class MonoDomainError(val domainError: Any) : RuntimeException()

fun Mono<ServerResponse>.handleErrors() = this.onErrorResume {
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

