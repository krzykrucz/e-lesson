package com.krzykrucz.elesson.currentlesson.attendance

import arrow.core.Either
import arrow.core.Option
import arrow.core.extensions.either.monad.flatten
import arrow.core.extensions.fx
import arrow.core.fix
import com.krzykrucz.elesson.currentlesson.shared.asyncMap
import java.time.LocalDateTime


typealias IsAttendanceChecked = Boolean
typealias NotePresentApi = suspend (AttendanceDto) -> Either<AttendanceError, IsAttendanceChecked>
typealias NoteAbsentApi = suspend (AttendanceDto) -> Either<AttendanceError, IsAttendanceChecked>
typealias HandleNoteLate = suspend (LateAttendanceDto) -> Either<AttendanceError, IsAttendanceChecked>

fun handleNotePresentDto(
    persistAttendance: PersistAttendance,
    fetchIncompleteAttendance: FetchIncompleteAttendance
): NotePresentApi = { attendanceDto ->
    val incompleteAttendanceDtoOpt = fetchIncompleteAttendance(attendanceDto.lessonId)

    val notePresenceResult = Option.fx {
        val (incompleteAttendance) = incompleteAttendanceDtoOpt
        notePresenceWorkflow()(
            attendanceDto.uncheckedStudent,
            incompleteAttendance.incompleteAttendanceList,
            incompleteAttendance.classRegistry
        )
    }.toEither { AttendanceError.LessonNotFound() }
        .flatten()
        .fix()

    notePresenceResult
        .asyncMap { persistAttendance(attendanceDto.lessonId, it) }
}

fun handleNoteAbsentDto(
    persistAttendance: PersistAttendance,
    fetchIncompleteAttendance: FetchIncompleteAttendance
): NoteAbsentApi = { attendanceDto ->
    val incompleteAttendanceDtoOpt = fetchIncompleteAttendance(attendanceDto.lessonId)

    val noteAbsenceResult = Option.fx {
        val (incompleteAttendance) = incompleteAttendanceDtoOpt
        noteAbsenceWorkflow()(
            attendanceDto.uncheckedStudent,
            incompleteAttendance.incompleteAttendanceList,
            incompleteAttendance.classRegistry
        )
    }.toEither { AttendanceError.LessonNotFound() }.flatten().fix()

    val persistResult =
        noteAbsenceResult
            .asyncMap { persistAttendance(attendanceDto.lessonId, it) }


    persistResult
}


fun handleLateAttendanceDto(
    persistAttendance: PersistAttendance,
    fetchCheckedAttendance: FetchCheckedAttendance
): HandleNoteLate = { lateAttendanceDto ->
    val checkedAttendanceOpt = fetchCheckedAttendance(lateAttendanceDto.lessonId)
    val noteLateResult = Option.fx {
        val (checkedAttendance) = checkedAttendanceOpt
        noteLateWorkflow()(
            lateAttendanceDto.lessonId.lessonHourNumber,
            lateAttendanceDto.absentStudent,
            checkedAttendance,
            LocalDateTime.parse(lateAttendanceDto.currentTime)
        )
    }.toEither { AttendanceError.LessonNotFound() }

    val persistResultAfterNoteLate =
        noteLateResult
            .asyncMap { persistAttendance(lateAttendanceDto.lessonId, it) }

    persistResultAfterNoteLate
}
