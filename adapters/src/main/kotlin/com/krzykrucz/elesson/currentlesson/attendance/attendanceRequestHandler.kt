package com.krzykrucz.elesson.currentlesson.attendance

import arrow.core.Either
import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.attendance.infrastructure.fetchClassRegistry
import com.krzykrucz.elesson.currentlesson.attendance.infrastructure.getLessonStartTime
import com.krzykrucz.elesson.currentlesson.attendance.infrastructure.persistCheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.*
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.ATTENDANCE_DATABASE
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.lessonIdOf
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.LocalDateTime


internal fun handleNoteAbsentRequest(): (ServerRequest) -> Mono<ServerResponse> {
    return { request ->
        request
                .bodyToMono(AttendanceDto::class.java)
                .map { attendanceDto ->
                    val classRegistryFromDb = fetchClassRegistry()(attendanceDto.className)
                    noteAbsence(
                            isInRegistry = isInRegistry(),
                            areAllStudentsChecked = areAllStudentsChecked()
                    )(
                            attendanceDto.uncheckedStudent,
                            attendanceDto.notCompletedAttendance,
                            classRegistryFromDb
                    )
                }
                .flatMap { result ->
                    when (result) {
                        is Either.Left -> ServerResponse
                                .badRequest()
                                .body(BodyInserters.fromObject(result.a))
                        is Either.Right -> ServerResponse
                                .ok()
                                .body(BodyInserters.fromObject(result.b))
                    }
                }
    }
}

internal fun handleNotePresentRequest(): (ServerRequest) -> Mono<ServerResponse> = { request ->
    request
            .bodyToMono(AttendanceDto::class.java)
            .map { attendanceDto ->
                val classRegistryFromDb = fetchClassRegistry()(attendanceDto.className)
                notePresence(
                        isInRegistry = isInRegistry(),
                        areAllStudentsChecked = areAllStudentsChecked()
                )(
                        attendanceDto.uncheckedStudent,
                        attendanceDto.notCompletedAttendance,
                        classRegistryFromDb
                )
            }
            .flatMap { result ->
                when (result) {
                    is Either.Left -> ServerResponse
                            .badRequest()
                            .body(BodyInserters.fromObject(result.a))
                    is Either.Right -> ServerResponse
                            .ok()
                            .body(BodyInserters.fromObject(result.b))
                }
            }
}

internal fun handleNoteLateRequest(): (ServerRequest) -> Mono<ServerResponse> = { request ->
    request
            .bodyToMono(LateAttendanceDto::class.java)
            .map { lateAttendanceDto ->
                noteLate(
                        isNotTooLate = isNotTooLate(getLessonStartTime())
                )(
                        lateAttendanceDto.lessonId,
                        lateAttendanceDto.absentStudent,
                        lateAttendanceDto.checkedAttendance,
                        LocalDateTime.parse(lateAttendanceDto.currentTime)
                )
            }
            .flatMap { updatedAttendance ->
                ServerResponse.ok().body(BodyInserters.fromObject(updatedAttendance))
            }
}

internal fun handleGetAttendanceRequest(): (ServerRequest) -> Mono<ServerResponse> = { request ->
    val date = request.queryParam("date").orElse("")
    val lessonHourNumber = request.queryParam("lessonHourNumber").map { it.toInt() }.orElse(0)
    val className = request.queryParam("className").orElse("")
    val lessonId = lessonIdOf(date, lessonHourNumber, className)
    val uncheckedAttendance = ATTENDANCE_DATABASE[lessonId].toOption()
    uncheckedAttendance
            .fold(
                    ifEmpty = { ServerResponse.noContent().build() },
                    ifSome = { attendance -> ServerResponse.ok().body(BodyInserters.fromObject(attendance)) }
            )
}

internal fun handleFinishAttendanceRequest(): (ServerRequest) -> Mono<ServerResponse> = { request ->
    request
            .bodyToMono(FinishAttendanceDto::class.java)
            .map { (lessonId, checkedAttendance) -> persistCheckedAttendance()(lessonId, checkedAttendance) }
            .flatMap { ServerResponse.ok().build() }
}