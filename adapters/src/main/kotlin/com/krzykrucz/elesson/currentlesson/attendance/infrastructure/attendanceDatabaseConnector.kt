package com.krzykrucz.elesson.currentlesson.attendance.infrastructure

import arrow.core.andThen
import arrow.core.toOption
import arrow.data.OptionT
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.applicative.applicative
import com.krzykrucz.elesson.currentlesson.attendance.AttendanceResponseDto
import com.krzykrucz.elesson.currentlesson.domain.attendance.*
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartedLesson
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.ATTENDANCE_DATABASE
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.STARTED_LESSON_DATABASE

typealias PersistAttendance = (Attendance) -> IO<AttendanceResponseDto>
typealias FetchAttendance = (LessonIdentifier) -> OptionT<ForIO, Attendance>
typealias FetchCheckedAttendance = (LessonIdentifier) -> IO<CheckedAttendance>
typealias FetchStartedLesson = (LessonIdentifier) -> IO<StartedLesson>
typealias FetchClassRegistry = (Attendance) -> IO<ClassRegistry>
typealias FetchStartedLessonAsAttendance = (LessonIdentifier) -> IO<NotCompletedAttendance>


fun fetchAttendance(): FetchAttendance = { lessonIdentifier ->
    OptionT.fromOption(IO.applicative(), ATTENDANCE_DATABASE.get(lessonIdentifier).toOption())
}

fun fetchCheckedAttendance(): FetchCheckedAttendance = { lessonIdentifier ->
    IO.just(ATTENDANCE_DATABASE.get(lessonIdentifier)!! as CheckedAttendance)
}

fun fetchStartedLesson(): FetchStartedLesson = { lessonIdentifier ->
    IO.just(STARTED_LESSON_DATABASE.get(lessonIdentifier)!!)
}

fun fetchStartedLessonAsAttendance(): FetchStartedLessonAsAttendance =
        fetchStartedLesson().andThen { startedLessonIo ->
            startedLessonIo.map { startedLesson ->
                startedLesson.toNotCompletedAttendance()
            }
        }

fun fetchClassRegistry(): FetchClassRegistry = { attendance ->
    fetchStartedLesson()(attendance.toLessonId())
            .map { it.clazz }
}

fun StartedLesson.toNotCompletedAttendance() = NotCompletedAttendance(attendance = AttendanceList(
        className = this.id.className,
        date = this.id.date,
        lessonHourNumber = this.id.lessonHourNumber
))

fun Attendance.toLessonId() = LessonIdentifier(
        date = this.attendance.date,
        lessonHourNumber = this.attendance.lessonHourNumber,
        className = this.attendance.className
)

fun getLessonStartTime(): GetLessonStartTime = { lessonHourNumber ->
    lessonHourNumber.getLessonScheduledStartTime()
}

fun persistAttendance(): PersistAttendance = { attendance ->
    IO.just(
            ATTENDANCE_DATABASE.put(attendance.toLessonId(), attendance)
    ).map {
        when (attendance) {
            is NotCompletedAttendance -> AttendanceResponseDto(checked = false)
            is CheckedAttendance -> AttendanceResponseDto(checked = true)
        }
    }
}