package com.krzykrucz.elesson.currentlesson.adapters.attendance.rest

import arrow.core.Either
import arrow.core.getOrElse
import arrow.fx.IO
import arrow.fx.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.adapters.attendance.AttendanceDto
import com.krzykrucz.elesson.currentlesson.adapters.attendance.AttendanceResponseDto
import com.krzykrucz.elesson.currentlesson.adapters.attendance.LateAttendanceDto
import com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase.HandleNoteAbsent
import com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase.HandleNoteLate
import com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase.HandleNotePresent
import com.krzykrucz.elesson.currentlesson.domain.attendance.AttendanceError
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

// TODO divide this config to 3 different adapters
@Configuration
class AttendanceRouterConfig {

    @Bean
    fun attendanceRouter(@Qualifier("noteAbsent") handleNoteAbsentDto: HandleNoteAbsent,
                         @Qualifier("notePresent") handleNotePresentDto: HandleNotePresent,
                         handleNoteLateDto: HandleNoteLate
    ) = router {
        (path("/attendance") and accept(MediaType.APPLICATION_JSON)).nest {
            POST("/absent") { request ->
                request.bodyToMono<AttendanceDto>()
                    .flatMap { dto ->
                        handleNoteAbsentDto(dto)
                            .map { it.toServerResponse() }
                            .run()
                    }
            }
            POST("/present") { request ->
                request.bodyToMono<AttendanceDto>()
                    .flatMap { dto ->
                        handleNotePresentDto(dto)
                            .map { it.toServerResponse() }
                            .run()
                    }
            }
            POST("/late") { request ->
                request.bodyToMono<LateAttendanceDto>()
                    .flatMap { dto ->
                        handleNoteLateDto(dto)
                            .map { it.toServerResponse() }
                            .run()
                    }
            }
        }
    }

    private fun Either<AttendanceError, Boolean>.toServerResponse(): Mono<ServerResponse> =
        when (this) {
            is Either.Left -> ServerResponse
                .badRequest()
                .body(BodyInserters.fromObject(this.a))
            is Either.Right ->
                ServerResponse
                    .ok()
                    .body(BodyInserters.fromObject(
                        AttendanceResponseDto(
                            this.b
                        )
                    ))
        }

    private fun IO<Mono<ServerResponse>>.run(): Mono<ServerResponse> =
        this.unsafeRunTimed(Duration(1, TimeUnit.SECONDS))
            .getOrElse { ServerResponse.badRequest().build() }
}
