package com.krzykrucz.elesson.currentlesson.adapters

import arrow.core.Left
import arrow.core.Right
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.domain.StartLessonError
import com.virtuslab.basetypes.result.Result
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

fun <A> IO<A>.toMono(): Mono<A> =
    Mono.create<A> { sink ->
        val dispose = unsafeRunAsyncCancellable { result ->
            result.fold(sink::error, sink::success)
        }
        sink.onCancel { dispose.invoke() }
    }

fun <A> Mono<A>.toIO(): IO<A> =
    IO.cancelable { cb ->
        val dispose = subscribe({ a -> cb(Right(a)) }, { e -> cb(Left(e)) })
        IO { dispose.dispose() }
    }

@Configuration
class StartLessonRouteAdapter {

    @Bean
    fun startLessonRoute(startLessonApi: StartLessonApi) =
        router {
            POST("/startlesson") { request ->
                request.bodyToMono(StartLessonRequest::class.java)
                    .toIO()
                    .flatMap(startLessonApi)
                    .toMono()
                    .flatMap {
                        when (val result = it) {
                            is Result.Success ->
                                ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(BodyInserters.fromObject(result.value))
                            is Result.Failure -> {
                                val status = when (result.error) {
                                    is StartLessonError.ExternalError,
                                    StartLessonError.ClassRegistryUnavailable -> HttpStatus.INTERNAL_SERVER_ERROR
                                    else -> HttpStatus.BAD_REQUEST
                                }
                                ServerResponse.status(status)
                                    .body(BodyInserters.fromObject(result.error.javaClass.simpleName))
                            }
                        }
                    }
            }
        }
}