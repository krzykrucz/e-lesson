package com.krzykrucz.elesson.currentlesson.attendance.infrastructure

import arrow.core.Option
import arrow.core.andThen
import arrow.core.toOption
import arrow.data.OptionT
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.applicative.applicative
import arrow.effects.extensions.io.functor.functor
import com.krzykrucz.elesson.currentlesson.attendance.AttendanceResponseDto
import com.krzykrucz.elesson.currentlesson.domain.attendance.*
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartedLesson
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.ATTENDANCE_DATABASE
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.STARTED_LESSON_DATABASE

typealias PersistAttendance = (Attendance) -> IO<AttendanceResponseDto>
typealias FetchAttendance = (LessonIdentifier) -> OptionT<ForIO, Attendance>
typealias FetchStartedLesson = (LessonIdentifier) -> OptionT<ForIO, StartedLesson>
typealias FetchClassRegistry = (Attendance) -> OptionT<ForIO, ClassRegistry>
typealias FetchStartedLessonAsAttendance = (LessonIdentifier) -> OptionT<ForIO, NotCompletedAttendance>
typealias FetchAttendanceIO = (LessonIdentifier) -> arrow.fx.IO<Option<Attendance>>


fun fetchAttendance(): FetchAttendance = { lessonIdentifier ->
    OptionT.fromOption(IO.applicative(), ATTENDANCE_DATABASE.get(lessonIdentifier).toOption())
}

fun fetchAttendanceIO(): FetchAttendanceIO = { lessonIdentifier ->
    arrow.fx.IO.just(ATTENDANCE_DATABASE.get(lessonIdentifier).toOption())
}

fun fetchStartedLesson(): FetchStartedLesson = { lessonIdentifier ->
    OptionT.fromOption(IO.applicative(), STARTED_LESSON_DATABASE.get(lessonIdentifier).toOption())
}

fun fetchStartedLessonAsAttendance(): FetchStartedLessonAsAttendance =
        fetchStartedLesson().andThen { startedLessonIo ->
            startedLessonIo.map(IO.functor()) { it.toNotCompletedAttendance() }
        }

fun fetchClassRegistry(): FetchClassRegistry = { attendance ->
    fetchStartedLesson()(attendance.toLessonId())
            .map(IO.functor()) { it.clazz }
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