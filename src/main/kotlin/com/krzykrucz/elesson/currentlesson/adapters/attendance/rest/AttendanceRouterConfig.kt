package com.krzykrucz.elesson.currentlesson.adapters.attendance.rest

import arrow.core.Either
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
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

// TODO divide this config to 3 different adapters
@Configuration
class AttendanceRouterConfig {

    @Bean
    fun attendanceRouter(@Qualifier("noteAbsent") handleNoteAbsentDto: HandleNoteAbsent,
                         @Qualifier("notePresent") handleNotePresentDto: HandleNotePresent,
                         handleNoteLateDto: HandleNoteLate
    ) = coRouter {
        (path("/attendance") and accept(MediaType.APPLICATION_JSON)).nest {
            POST("/absent") { request ->
                val dto = request.awaitBody<AttendanceDto>()
                handleNoteAbsentDto(dto)
                    .toServerResponse()
            }
            POST("/present") { request ->
                val dto = request.awaitBody<AttendanceDto>()
                handleNotePresentDto(dto)
                    .toServerResponse()
            }
            POST("/late") { request ->
                val dto = request.awaitBody<LateAttendanceDto>()
                handleNoteLateDto(dto)
                    .toServerResponse()
            }
        }
    }

    private suspend fun Either<AttendanceError, Boolean>.toServerResponse(): ServerResponse =
        when (this) {
            is Either.Left -> ServerResponse
                .badRequest()
                .bodyValueAndAwait(this.a)
            is Either.Right ->
                ServerResponse
                    .ok()
                    .bodyValueAndAwait(
                        AttendanceResponseDto(
                            this.b
                        )
                    )
        }

}
