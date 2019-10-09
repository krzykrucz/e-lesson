package com.krzykrucz.elesson.currentlesson.attendance

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.toOption
import arrow.effects.IO
import arrow.effects.extensions.io.monad.map
import arrow.effects.extensions.io.monad.monad
import arrow.effects.fix
import arrow.effects.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.attendance.infrastructure.*
import com.krzykrucz.elesson.currentlesson.domain.attendance.*
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.ATTENDANCE_DATABASE
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.lessonIdOf
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


internal fun handleNoteAbsentRequest(): (ServerRequest) -> Mono<ServerResponse> {
    return { request ->
        request
                .bodyToMono(AttendanceDto::class.java)
                .map { (student, lessonId) ->
                    fetchNotCompletedAttendance(lessonId, student)
                }
                .map { attendanceIo ->
                    getClassRegistry(attendanceIo)
                            .map { (student, attendance, classRegistry) ->
                                noteAbsence(
                                        isInRegistry = isInRegistry(),
                                        areAllStudentsChecked = areAllStudentsChecked()
                                )(
                                        student,
                                        attendance,
                                        classRegistry
                                )
                            }
                            .map { notingResult ->
                                notingResult.map { attendance ->
                                    persistAttendance()(attendance)
                                }

                            }
                }
                .flatMap { resultIO ->
                    resultIO.map { result ->
                        handleNotingResult(result)
                    }.run()
                }
    }
}

internal fun handleNotePresentRequest(): (ServerRequest) -> Mono<ServerResponse> = { request ->
    request
            .bodyToMono(AttendanceDto::class.java)
            .map { (student, lessonId) ->
                fetchNotCompletedAttendance(lessonId, student)
            }
            .map { attendanceIo ->
                getClassRegistry(attendanceIo)
                        .map { (student, attendance, classRegistry) ->
                            notePresence(
                                    isInRegistry = isInRegistry(),
                                    areAllStudentsChecked = areAllStudentsChecked()
                            )(
                                    student,
                                    attendance,
                                    classRegistry
                            )
                        }
                        .map { notingResult ->
                            notingResult.map { attendance ->
                                persistAttendance()(attendance)
                            }
                        }
            }
            .flatMap { resultIO ->
                resultIO.map { result ->
                    handleNotingResult(result)
                }.run()
            }
}

internal fun handleNoteLateRequest(): (ServerRequest) -> Mono<ServerResponse> = { request ->
    request
            .bodyToMono(LateAttendanceDto::class.java)
            .map { lateAttendanceDto ->
                fetchCheckedAttendance()(lateAttendanceDto.lessonId)
                        .map { checkedAttendance ->
                            noteLate(
                                    isNotTooLate = isNotTooLate(getLessonStartTime())
                            )(
                                    lateAttendanceDto.lessonId,
                                    lateAttendanceDto.absentStudent,
                                    checkedAttendance,
                                    LocalDateTime.parse(lateAttendanceDto.currentTime)
                            )
                        }
                        .flatMap { checkedAttendance -> persistAttendance()(checkedAttendance) }
            }
            .flatMap { updatedAttendanceIO ->
                updatedAttendanceIO.map { updatedAttendance ->
                    ServerResponse.ok().body(BodyInserters.fromObject(updatedAttendance))
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

private fun handleNotingResult(result: Either<AttendanceError, IO<AttendanceResponseDto>>): Mono<ServerResponse> {
    return when (result) {
        is Either.Left -> ServerResponse
                .badRequest()
                .body(BodyInserters.fromObject(result.a))
        is Either.Right -> result.b.map {
            ServerResponse
                    .ok()
                    .body(BodyInserters.fromObject(it))
        }.run()
    }
}

private fun getClassRegistry(attendanceIo: IO<Pair<UncheckedStudent, NotCompletedAttendance>>): IO<Triple<UncheckedStudent, NotCompletedAttendance, ClassRegistry>> {
    return attendanceIo
            .flatMap { (student, attendance) ->
                fetchClassRegistry()(attendance)
                        .map { classRegistry -> Triple(student, attendance, classRegistry) }
            }
}

private fun fetchNotCompletedAttendance(lessonId: LessonIdentifier, student: UncheckedStudent): IO<Pair<UncheckedStudent, NotCompletedAttendance>> {
    return fetchAttendance()(lessonId)
            .getOrElseF(IO.monad()) { fetchStartedLessonAsAttendance()(lessonId) }
            .map { student to it as NotCompletedAttendance }
            .fix()
}

private fun IO<Mono<ServerResponse>>.run(): Mono<ServerResponse> =
        this.unsafeRunTimed(Duration(3, TimeUnit.SECONDS))
                .getOrElse { ServerResponse.badRequest().build() }
