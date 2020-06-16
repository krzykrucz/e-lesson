package com.krzykrucz.elesson.currentlesson.attendance

import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.RestApi
import com.krzykrucz.elesson.currentlesson.shared.toServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter

fun attendanceRouter(
    noteAbsentApiDto: NoteAbsentApi,
    notePresentApi: NotePresentApi,
    handleNoteLateDto: HandleNoteLate
) = coRouter {
    "/attendance".nest {
        POST("/absent", handleAttentanceAbsent(noteAbsentApiDto))
        POST("/present", handleAttentancePresent(notePresentApi))
        POST("/late", handleAttentanceLate(handleNoteLateDto))
    }
}


private fun handleAttentanceAbsent(noteAbsentApiDto: NoteAbsentApi): RestApi = { request ->
    val dto = request.awaitBody<AttendanceDto>()
    noteAbsentApiDto(dto)
        .map { AttendanceResponseDto(it) }
        .toServerResponse()
}

private fun handleAttentancePresent(notePresentApi: NotePresentApi): RestApi = { request ->
    val dto = request.awaitBody<AttendanceDto>()
    notePresentApi(dto)
        .map { AttendanceResponseDto(it) }
        .toServerResponse()
}

private fun handleAttentanceLate(handleNoteLateDto: HandleNoteLate): RestApi = { request ->
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
