package com.krzykrucz.elesson.currentlesson.preparedness.domain

import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.PresentStudent
import com.krzykrucz.elesson.currentlesson.preparedness.domain.UnpreparednessError.AlreadyRaised
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
    object Unknown : UnpreparednessError()
}
//dependency
typealias CheckNumberOfTimesStudentWasUnpreparedInSemester = (PresentStudent, ClassName) -> AsyncOutput<StudentSubjectUnpreparednessInASemester, StudentInSemesterReadError>

typealias HasStudentUsedAllUnpreparednesses = (StudentSubjectUnpreparednessInASemester) -> Boolean

typealias HasStudentAlreadyRaisedUnprepared = (StudentsUnpreparedForLesson, PresentStudent) -> Boolean

typealias CheckStudentIsPresent = (StudentReportingUnpreparedness, CheckedAttendanceList) -> Output<PresentStudent, StudentNotPresent>

typealias AreStudentsEqual = (PresentStudent, StudentReportingUnpreparedness) -> Boolean

//workflows
typealias CheckStudentCanReportUnprepared = (PresentStudent, ClassName) -> AsyncOutput<PresentStudent, UnpreparednessError>

typealias NoteStudentUnpreparedForLesson = (PresentStudent, StudentsUnpreparedForLesson) -> Output<StudentsUnpreparedForLesson, AlreadyRaised>

typealias CreateEvent = (LessonIdentifier, StudentsUnpreparedForLesson) -> StudentMarkedUnprepared

//pipeline
data class StudentReportingUnpreparedness(
        val firstName: FirstName,
        val secondName: SecondName
)

//event
data class StudentMarkedUnprepared(
        val lessonId: LessonIdentifier,
        val happenedAt: LocalDateTime = LocalDateTime.now(),
        val unpreparedStudent: UnpreparedStudent,
        val studentsUnpreparedForLesson: StudentsUnpreparedForLesson
)

typealias ReportUnpreparedness = (StudentReportingUnpreparedness, CurrentLesson) -> AsyncOutput<StudentMarkedUnprepared, UnpreparednessError>
