package com.krzykrucz.elesson.currentlesson.preparedness.domain

import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.PresentStudent
import com.krzykrucz.elesson.currentlesson.preparedness.domain.UnpreparednessError.AlreadyRaised
import com.krzykrucz.elesson.currentlesson.preparedness.domain.UnpreparednessError.LessonNotStarted
import com.krzykrucz.elesson.currentlesson.preparedness.domain.UnpreparednessError.StudentNotPresent
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentInSemesterReadError
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.Output
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import java.time.LocalDateTime

//types
data class UnpreparedStudent(
        val firstName: FirstName,
        val secondName: SecondName
)

data class StudentsUnpreparedForLesson(val students: List<UnpreparedStudent> = emptyList())

sealed class UnpreparednessError {
    object AlreadyRaised : UnpreparednessError()
    object UnpreparedTooManyTimes : UnpreparednessError()
    object TooLateToRaiseUnpreparedness : UnpreparednessError()
    object StudentNotPresent : UnpreparednessError()
    object LessonNotStarted: UnpreparednessError()
}

data class StudentReportingUnpreparedness(
        val firstName: String,
        val secondName: String
)

//dependency
typealias CheckNumberOfTimesStudentWasUnpreparedInSemester = (PresentStudent, ClassName) -> AsyncOutput<StudentSubjectUnpreparednessInASemester, StudentInSemesterReadError>

typealias HasStudentUsedAllUnpreparednesses = (StudentSubjectUnpreparednessInASemester) -> Boolean

typealias HasStudentAlreadyRaisedUnprepared = (StudentsUnpreparedForLesson, PresentStudent) -> Boolean

typealias CheckStudentIsPresent = (StudentReportingUnpreparedness, CheckedAttendanceList) -> Output<PresentStudent, StudentNotPresent>

typealias AreStudentsEqual = (PresentStudent, StudentReportingUnpreparedness) -> Boolean

//persistence
typealias PersistUnpreparedStudentToLesson = (StudentMarkedUnprepared) -> IO<LessonIdentifier>

typealias NotifyStudentMarkedUnprepared = (StudentMarkedUnprepared) -> IO<Unit>

typealias FindCurrentLesson = (LessonIdentifier) -> AsyncOutput<CurrentLesson, LessonNotStarted>

//workflows
typealias CheckStudentCanReportUnprepared = (PresentStudent, ClassName) -> AsyncOutput<PresentStudent, UnpreparednessError>

typealias NoteStudentUnpreparedForLesson = (PresentStudent, StudentsUnpreparedForLesson) -> Output<StudentsUnpreparedForLesson, AlreadyRaised>

typealias CreateEvent = (LessonIdentifier, StudentsUnpreparedForLesson) -> StudentMarkedUnprepared

//event
data class StudentMarkedUnprepared(
        val lessonId: LessonIdentifier,
        val happenedAt: LocalDateTime = LocalDateTime.now(),
        val unpreparedStudent: UnpreparedStudent,
        val studentsUnpreparedForLesson: StudentsUnpreparedForLesson
)

//pipeline
typealias ReportUnpreparedness = (StudentReportingUnpreparedness, CurrentLesson) -> AsyncOutput<StudentMarkedUnprepared, UnpreparednessError>
