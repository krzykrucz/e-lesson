package com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase

import arrow.core.Either
import arrow.core.Option
import arrow.core.extensions.either.monad.flatten
import arrow.core.extensions.fx
import arrow.core.fix
import com.krzykrucz.elesson.currentlesson.adapters.asyncMap
import com.krzykrucz.elesson.currentlesson.adapters.attendance.AttendanceDto
import com.krzykrucz.elesson.currentlesson.adapters.attendance.LateAttendanceDto
import com.krzykrucz.elesson.currentlesson.domain.attendance.AttendanceError
import com.krzykrucz.elesson.currentlesson.domain.attendance.FetchCheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.FetchIncompleteAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.PersistAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.addAbsentStudent
import com.krzykrucz.elesson.currentlesson.domain.attendance.addPresentStudent
import com.krzykrucz.elesson.currentlesson.domain.attendance.completeList
import com.krzykrucz.elesson.currentlesson.domain.attendance.getLessonStartTime
import com.krzykrucz.elesson.currentlesson.domain.attendance.isInRegistry
import com.krzykrucz.elesson.currentlesson.domain.attendance.isNotTooLate
import com.krzykrucz.elesson.currentlesson.domain.attendance.noteAbsence
import com.krzykrucz.elesson.currentlesson.domain.attendance.noteLate
import com.krzykrucz.elesson.currentlesson.domain.attendance.notePresence
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime


typealias IsAttendanceChecked = Boolean
typealias HandleNotePresent = suspend (AttendanceDto) -> Either<AttendanceError, IsAttendanceChecked>
typealias HandleNoteAbsent = suspend (AttendanceDto) -> Either<AttendanceError, IsAttendanceChecked>
typealias HandleNoteLate = suspend (LateAttendanceDto) -> Either<AttendanceError, IsAttendanceChecked>

@Configuration
class AttendanceHandler(
    val persistAttendance: PersistAttendance,
    val fetchIncompleteAttendance: FetchIncompleteAttendance,
    val fetchCheckedAttendance: FetchCheckedAttendance
) {

    @Bean("notePresent")
    fun handleNotePresentDto(): HandleNotePresent = { attendanceDto ->
        val incompleteAttendanceDtoOpt = fetchIncompleteAttendance(attendanceDto.lessonId)

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
        }.toEither { AttendanceError.LessonNotFound() }
            .flatten()
            .fix()

        notePresenceResult
            .asyncMap { persistAttendance(attendanceDto.lessonId, it) }
    }

    @Bean("noteAbsent")
    fun handleNoteAbsentDto(): HandleNoteAbsent = { attendanceDto ->
        val incompleteAttendanceDtoOpt = fetchIncompleteAttendance(attendanceDto.lessonId)

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

        val persistResult =
            noteAbsenceResult
                .asyncMap { persistAttendance(attendanceDto.lessonId, it) }


        persistResult
    }


    @Bean
    fun handleLateAttendanceDto(): HandleNoteLate = { lateAttendanceDto ->
        val checkedAttendanceOpt = fetchCheckedAttendance(lateAttendanceDto.lessonId)
        val noteLateResult = Option.fx {
            val (checkedAttendance) = checkedAttendanceOpt
            noteLate(
                isNotTooLate(getLessonStartTime())
            )(
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
}
