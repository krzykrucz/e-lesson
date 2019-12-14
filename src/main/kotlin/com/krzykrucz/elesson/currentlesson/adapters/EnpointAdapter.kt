package com.krzykrucz.elesson.currentlesson.adapters

import arrow.core.Left
import arrow.core.Right
import arrow.fx.IO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

// TODO use this
fun <A> IO<A>.toMono(): Mono<A> =
    Mono.create<A> { sink ->
        val dispose = unsafeRunAsyncCancellable { result ->
            result.fold(sink::error, sink::success)
        }
        sink.onCancel { dispose.invoke() }
    }

// TODO use this
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
                    // TODO
                    .flatMap {
                        // TODO:
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromObject(it))
                    }
            }
        }
}
