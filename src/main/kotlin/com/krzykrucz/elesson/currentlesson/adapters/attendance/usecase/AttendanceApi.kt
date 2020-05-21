package com.krzykrucz.elesson.currentlesson.adapters.attendance.usecase

import arrow.core.Either
import arrow.core.Option
import arrow.core.extensions.either.monad.flatten
import arrow.core.extensions.fx
import arrow.core.fix
import com.krzykrucz.elesson.currentlesson.adapters.asyncMap
import com.krzykrucz.elesson.currentlesson.adapters.attendance.rest.AttendanceDto
import com.krzykrucz.elesson.currentlesson.adapters.attendance.rest.LateAttendanceDto
import com.krzykrucz.elesson.currentlesson.domain.attendance.AttendanceError
import com.krzykrucz.elesson.currentlesson.domain.attendance.FetchCheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.FetchIncompleteAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.PersistAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.noteAbsenceWorkflow
import com.krzykrucz.elesson.currentlesson.domain.attendance.noteLateWorkflow
import com.krzykrucz.elesson.currentlesson.domain.attendance.notePresenceWorkflow
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime


typealias IsAttendanceChecked = Boolean
typealias NotePresentApi = suspend (AttendanceDto) -> Either<AttendanceError, IsAttendanceChecked>
typealias NoteAbsentApi = suspend (AttendanceDto) -> Either<AttendanceError, IsAttendanceChecked>
typealias HandleNoteLate = suspend (LateAttendanceDto) -> Either<AttendanceError, IsAttendanceChecked>

@Configuration
class AttendanceHandler(
    val persistAttendance: PersistAttendance,
    val fetchIncompleteAttendance: FetchIncompleteAttendance,
    val fetchCheckedAttendance: FetchCheckedAttendance
) {

    @Bean("notePresent")
    fun handleNotePresentDto(): NotePresentApi = { attendanceDto ->
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

    @Bean("noteAbsent")
    fun handleNoteAbsentDto(): NoteAbsentApi = { attendanceDto ->
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


    @Bean
    fun handleLateAttendanceDto(): HandleNoteLate = { lateAttendanceDto ->
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
}
