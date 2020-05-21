package com.krzykrucz.elesson.currentlesson.adapters.attendance.rest

import com.krzykrucz.elesson.currentlesson.adapters.RestApi
import com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase.HandleNoteLate
import com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase.NoteAbsentApi
import com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase.NotePresentApi
import com.krzykrucz.elesson.currentlesson.adapters.toServerResponse
import com.krzykrucz.elesson.currentlesson.domain.attendance.AbsentStudent
import com.krzykrucz.elesson.currentlesson.domain.attendance.UncheckedStudent
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class AttendanceRouterConfig {

    @Bean
    fun attendanceRouter(@Qualifier("noteAbsent") noteAbsentApiDto: NoteAbsentApi,
                         @Qualifier("notePresent") notePresentApi: NotePresentApi,
                         handleNoteLateDto: HandleNoteLate
    ) = coRouter {
        "/attendance".nest {
            POST("/absent", handleAttentanceAbsent(noteAbsentApiDto))
            POST("/present", handleAttentancePresent(notePresentApi))
            POST("/late", handleAttentanceLate(handleNoteLateDto))
        }
    }

}

fun handleAttentanceAbsent(noteAbsentApiDto: NoteAbsentApi): RestApi = { request ->
    val dto = request.awaitBody<AttendanceDto>()
    noteAbsentApiDto(dto)
        .map { AttendanceResponseDto(it) }
        .toServerResponse()
}

fun handleAttentancePresent(notePresentApi: NotePresentApi): RestApi = { request ->
    val dto = request.awaitBody<AttendanceDto>()
    notePresentApi(dto)
        .map { AttendanceResponseDto(it) }
        .toServerResponse()
}

fun handleAttentanceLate(handleNoteLateDto: HandleNoteLate): RestApi = { request ->
    val dto = request.awaitBody<LateAttendanceDto>()
    handleNoteLateDto(dto)
        .map { AttendanceResponseDto(it) }
        .toServerResponse()
}

data class AttendanceDto(
    val uncheckedStudent: UncheckedStudent,
    val lessonId: LessonIdentifier
)

data class AttendanceResponseDto(
    val checked: Boolean
)

data class LateAttendanceDto(
    val lessonId: LessonIdentifier,
    val absentStudent: AbsentStudent,
    val currentTime: String
)
