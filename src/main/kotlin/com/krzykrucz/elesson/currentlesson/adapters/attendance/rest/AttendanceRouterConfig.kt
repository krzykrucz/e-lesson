package com.krzykrucz.elesson.currentlesson.adapters.attendance.rest

import com.krzykrucz.elesson.currentlesson.adapters.AsyncRequestHandler
import com.krzykrucz.elesson.currentlesson.adapters.attendance.AttendanceDto
import com.krzykrucz.elesson.currentlesson.adapters.attendance.AttendanceResponseDto
import com.krzykrucz.elesson.currentlesson.adapters.attendance.LateAttendanceDto
import com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase.HandleNoteAbsent
import com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase.HandleNoteLate
import com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase.HandleNotePresent
import com.krzykrucz.elesson.currentlesson.adapters.toServerResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter

// TODO divide this config to 3 different adapters
@Configuration
class AttendanceRouterConfig {

    @Bean
    fun attendanceRouter(@Qualifier("noteAbsent") handleNoteAbsentDto: HandleNoteAbsent,
                         @Qualifier("notePresent") handleNotePresentDto: HandleNotePresent,
                         handleNoteLateDto: HandleNoteLate
    ) = coRouter {
        "/attendance".nest {
            POST("/absent", handleAttentanceAbsent(handleNoteAbsentDto))
            POST("/present", handleAttentancePresent(handleNotePresentDto))
            POST("/late", handleAttentanceLate(handleNoteLateDto))
        }
    }

}

fun handleAttentanceAbsent(handleNoteAbsentDto: HandleNoteAbsent): AsyncRequestHandler = { request ->
    val dto = request.awaitBody<AttendanceDto>()
    handleNoteAbsentDto(dto)
        .map { AttendanceResponseDto(it) }
        .toServerResponse()
}

fun handleAttentancePresent(handleNotePresentDto: HandleNotePresent): AsyncRequestHandler = { request ->
    val dto = request.awaitBody<AttendanceDto>()
    handleNotePresentDto(dto)
        .map { AttendanceResponseDto(it) }
        .toServerResponse()
}

fun handleAttentanceLate(handleNoteLateDto: HandleNoteLate): AsyncRequestHandler = { request ->
    val dto = request.awaitBody<LateAttendanceDto>()
    handleNoteLateDto(dto)
        .map { AttendanceResponseDto(it) }
        .toServerResponse()
}
