package com.krzykrucz.elesson.currentlesson.attendance.adapters.rest

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.getOrElse
import arrow.core.toOption
import arrow.data.OptionT
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.functor.functor
import arrow.effects.fix
import arrow.effects.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.attendance.domain.AttendanceError
import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchCheckedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchNotCompletedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.GetLessonStartTime
import com.krzykrucz.elesson.currentlesson.attendance.domain.PersistAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.areAllStudentsChecked
import com.krzykrucz.elesson.currentlesson.attendance.domain.isInRegistry
import com.krzykrucz.elesson.currentlesson.attendance.domain.isNotTooLate
import com.krzykrucz.elesson.currentlesson.attendance.domain.noteAbsence
import com.krzykrucz.elesson.currentlesson.attendance.domain.noteLate
import com.krzykrucz.elesson.currentlesson.attendance.domain.notePresence
import com.krzykrucz.elesson.currentlesson.monolith.Database.Companion.ATTENDANCE_DATABASE
import com.krzykrucz.elesson.currentlesson.monolith.Database.Companion.lessonIdOf
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


internal fun handleNoteAbsentRequest(
        persistAttendance: PersistAttendance,
        fetchNotCompletedAttendance: FetchNotCompletedAttendance
): (ServerRequest) -> Mono<ServerResponse> {
    return { request ->
        request
                .bodyToMono(AttendanceDto::class.java)
                .map { (student, lessonId) ->
                    fetchNotCompletedAttendance(lessonId)
                            .map(IO.functor()) { Tuple2(student, it) }
                }
                .map {
                    it.map(IO.functor()) { (student, attendance) ->
                        noteAbsence(
                                isInRegistry = isInRegistry(),
                                areAllStudentsChecked = areAllStudentsChecked()
                        )(
                                student,
                                attendance
                        )
                    }.map(IO.functor()) { notingResult ->
                        notingResult.map { attendance ->
                            persistAttendance(attendance)
                                    .map(::AttendanceResponseDto)
                        }
                    }
                }
                .flatMap {
                    it.map(IO.functor()) { result ->
                        handleNotingResult(result)
                    }.run()
                }
    }
}

internal fun handleNotePresentRequest(
        persistAttendance: PersistAttendance,
        fetchNotCompletedAttendance: FetchNotCompletedAttendance
): (ServerRequest) -> Mono<ServerResponse> = {
    it.bodyToMono(AttendanceDto::class.java)
            .map { (student, lessonId) ->
                fetchNotCompletedAttendance(lessonId)
                        .map(IO.functor()) { Tuple2(student, it) }
            }
            .map {
                it.map(IO.functor()) { (student, attendance) ->
                    notePresence(
                            isInRegistry = isInRegistry(),
                            areAllStudentsChecked = areAllStudentsChecked()
                    )(
                            student,
                            attendance
                    )
                }.map(IO.functor()) { notingResult ->
                    notingResult.map { attendance ->
                        persistAttendance(attendance)
                                .map(::AttendanceResponseDto)
                    }
                }
            }
            .flatMap {
                it.map(IO.functor()) { result ->
                    handleNotingResult(result)
                }.run()
            }
}

internal fun handleNoteLateRequest(
        persistAttendance: PersistAttendance,
        fetchCheckedAttendance: FetchCheckedAttendance
): (ServerRequest) -> Mono<ServerResponse> = { request ->
    request
            .bodyToMono(LateAttendanceDto::class.java)
            .map { lateAttendanceDto ->
                fetchCheckedAttendance(lateAttendanceDto.lessonId)
                        .map(IO.functor()) { checkedAttendance ->
                            noteLate(
                                    isNotTooLate = isNotTooLate(getLessonStartTime())
                            )(
                                    lateAttendanceDto.lessonId,
                                    lateAttendanceDto.absentStudent,
                                    checkedAttendance,
                                    LocalDateTime.parse(lateAttendanceDto.currentTime)
                            )
                        }
                        .map(IO.functor()) { checkedAttendance -> persistAttendance(checkedAttendance) }
            }
            .flatMap {
                it.map(IO.functor()) { attendanceIO ->
                    attendanceIO
                            .map(::AttendanceResponseDto)
                            .map { updatedAttendance ->
                                ServerResponse.ok().body(BodyInserters.fromObject(updatedAttendance))
                            }.run()
                }.run()
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

private fun handleNotingResult(result: Either<AttendanceError, IO<AttendanceResponseDto>>): Mono<ServerResponse> =
        when (result) {
            is Either.Left -> ServerResponse
                    .badRequest()
                    .body(BodyInserters.fromObject(result.a))
            is Either.Right -> result.b.map {
                ServerResponse
                        .ok()
                        .body(BodyInserters.fromObject(it))
            }.run()
        }

private fun OptionT<ForIO, Mono<ServerResponse>>.run(): Mono<ServerResponse> =
        this.getOrElse(IO.functor()) { ServerResponse.noContent().build() }
                .fix().run()

private fun IO<Mono<ServerResponse>>.run(): Mono<ServerResponse> =
        this.unsafeRunTimed(Duration(3, TimeUnit.SECONDS))
                .getOrElse { ServerResponse.badRequest().build() }

private fun getLessonStartTime(): GetLessonStartTime = { lessonHourNumber ->
    lessonHourNumber.getLessonScheduledStartTime()
}