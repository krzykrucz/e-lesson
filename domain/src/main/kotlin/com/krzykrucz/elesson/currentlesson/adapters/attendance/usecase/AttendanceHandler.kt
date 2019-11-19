package com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase

import arrow.core.Either
import arrow.core.Option
import arrow.core.extensions.either.monad.flatten
import arrow.core.extensions.either.traverse.sequence
import arrow.core.extensions.fx
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix
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
typealias HandleNotePresent = (AttendanceDto) -> IO<Either<AttendanceError, IsAttendanceChecked>>
typealias HandleNoteAbsent = (AttendanceDto) -> IO<Either<AttendanceError, IsAttendanceChecked>>
typealias HandleNoteLate = (LateAttendanceDto) -> IO<Either<AttendanceError, IsAttendanceChecked>>

@Configuration
class AttendanceHandler(
    val persistAttendance: PersistAttendance,
    val fetchIncompleteAttendance: FetchIncompleteAttendance,
    val fetchCheckedAttendance: FetchCheckedAttendance
) {

    @Bean("notePresent")
    fun handleNotePresentDto(): HandleNotePresent = { attendanceDto ->
        IO.fx {
            val (incompleteAttendanceDtoOpt) = fetchIncompleteAttendance(attendanceDto.lessonId)

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
                .map { persistAttendance(attendanceDto.lessonId, it) }
                .sequence()

            persistResult
        }
    }

    @Bean("noteAbsent")
    fun handleNoteAbsentDto(): HandleNoteAbsent = { attendanceDto ->
        IO.fx {
            val (incompleteAttendanceDtoOpt) = fetchIncompleteAttendance(attendanceDto.lessonId)

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
                    .map { persistAttendance(attendanceDto.lessonId, it) }
                    .sequence()


            persistResult
        }
    }

    @Bean
    fun handleLateAttendanceDto(): HandleNoteLate = { lateAttendanceDto ->
        IO.fx {
            val (checkedAttendanceOpt) = fetchCheckedAttendance(lateAttendanceDto.lessonId)
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

            val (persistResultAfterNoteLate) = noteLateResult
                .map { persistAttendance(lateAttendanceDto.lessonId, it) }
                .sequence()

            persistResultAfterNoteLate
        }
    }

    private fun Either<AttendanceError, IO<Boolean>>.sequence(): IO<Either<AttendanceError, Boolean>> =
        this.sequence(IO.applicative()).fix()
            .map { it.fix() }
}
