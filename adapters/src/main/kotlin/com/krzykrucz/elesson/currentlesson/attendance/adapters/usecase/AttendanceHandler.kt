package com.krzykrucz.elesson.currentlesson.attendance.adapters.usecase

import arrow.core.*
import arrow.core.extensions.either.monad.flatten
import arrow.core.extensions.either.traverse.sequence
import arrow.core.extensions.fx
import arrow.core.extensions.sequence
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix
import com.krzykrucz.elesson.currentlesson.attendance.adapters.persistence.fetchCheckedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.adapters.persistence.fetchIncompleteAttendance
import com.krzykrucz.elesson.currentlesson.attendance.adapters.persistence.persistAttendance
import com.krzykrucz.elesson.currentlesson.attendance.adapters.rest.AttendanceDto
import com.krzykrucz.elesson.currentlesson.attendance.adapters.rest.AttendanceResponseDto
import com.krzykrucz.elesson.currentlesson.attendance.adapters.rest.LateAttendanceDto
import com.krzykrucz.elesson.currentlesson.attendance.domain.*
import com.krzykrucz.elesson.currentlesson.monolith.Database
import com.krzykrucz.elesson.currentlesson.monolith.Database.Companion.lessonIdOf
import com.krzykrucz.elesson.currentlesson.monolith.PersistentCurrentLesson
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.LocalDateTime


fun handleNotePresentDto(attendanceDto: AttendanceDto): IO<Mono<ServerResponse>> =
    IO.fx {
        val (incompleteAttendanceDtoOpt) = fetchIncompleteAttendance()(attendanceDto.lessonId)

        val notePresenceResult = Option.fx {
            val (incompleteAttendance) = incompleteAttendanceDtoOpt
            notePresence(
                isInRegistry(),
                completeList(),
                addPresentStudent()
            )(
                attendanceDto.uncheckedStudent,
                incompleteAttendance.incompleteAttendanceList,
                incompleteAttendance.classRegistry
            )
        }.toEither { AttendanceError.LessonNotFound() }.flatten().fix()

        val (persistResult) = notePresenceResult
            .map { persistAttendance()(attendanceDto.lessonId, it) }
            .sequence()

        persistResult.toServerResponse()
    }

fun handleNoteAbsentDto(attendanceDto: AttendanceDto): IO<Mono<ServerResponse>> =
    IO.fx {
        val (incompleteAttendanceDtoOpt) = fetchIncompleteAttendance()(attendanceDto.lessonId)

        val noteAbsenceResult = Option.fx {
            val (incompleteAttendance) = incompleteAttendanceDtoOpt
            noteAbsence(
                isInRegistry(),
                completeList(),
                addAbsentStudent()
            )(
                attendanceDto.uncheckedStudent,
                incompleteAttendance.incompleteAttendanceList,
                incompleteAttendance.classRegistry
            )
        }.toEither { AttendanceError.LessonNotFound() }.flatten().fix()

        val (persistResult) =
            noteAbsenceResult
                .map { persistAttendance()(attendanceDto.lessonId, it) }
                .sequence()


        persistResult.toServerResponse()
    }

fun handleLateAttendanceDto(lateAttendanceDto: LateAttendanceDto): IO<Mono<ServerResponse>> =
    IO.fx {
        val (checkedAttendanceOpt) = fetchCheckedAttendance()(lateAttendanceDto.lessonId)
        val (persistResultAfterNoteLate) = Option.fx {
            val (checkedAttendance) = checkedAttendanceOpt
            val updatedAttendance = noteLate(
                isNotTooLate(getLessonStartTime())
            )(
                lateAttendanceDto.lessonId.lessonHourNumber,
                lateAttendanceDto.absentStudent,
                checkedAttendance,
                LocalDateTime.parse(lateAttendanceDto.currentTime)
            )

            persistAttendance()(lateAttendanceDto.lessonId, updatedAttendance)
        }.sequence(IO.applicative()).fix()

        persistResultAfterNoteLate
            .map { AttendanceResponseDto(it) }
            .map { ServerResponse.ok().body(BodyInserters.fromObject(it)) }
            .getOrElse { ServerResponse.noContent().build() }
    }

internal fun handleGetAttendanceRequest(): (ServerRequest) -> Mono<ServerResponse> = { request ->
    val date = request.queryParam("date").orElse("")
    val lessonHourNumber = request.queryParam("lessonHourNumber").map { it.toInt() }.orElse(0)
    val className = request.queryParam("className").orElse("")
    val lessonId = lessonIdOf(date, lessonHourNumber, className)
    val uncheckedAttendance = Database.LESSON_DATABASE[lessonId].toOption()
        .map(PersistentCurrentLesson::attendance)
        .flatMap { Option.fromNullable(it) }
    uncheckedAttendance
        .fold(
            ifEmpty = { ServerResponse.noContent().build() },
            ifSome = { attendance -> ServerResponse.ok().body(BodyInserters.fromObject(attendance)) }
        )
}

private fun Either<AttendanceError, Boolean>.toServerResponse(): Mono<ServerResponse> =
    when (this) {
        is Either.Left -> ServerResponse
            .badRequest()
            .body(BodyInserters.fromObject(this.a))
        is Either.Right ->
            ServerResponse
                .ok()
                .body(BodyInserters.fromObject(AttendanceResponseDto(this.b)))
    }

private fun Either<AttendanceError, IO<Boolean>>.sequence(): IO<Either<AttendanceError, Boolean>> =
    this.sequence(IO.applicative()).fix()
        .map { it.fix() }
