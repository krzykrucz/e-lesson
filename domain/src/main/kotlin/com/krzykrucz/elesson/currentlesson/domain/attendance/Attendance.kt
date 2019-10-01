package com.krzykrucz.elesson.currentlesson.domain.attendance

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.startlesson.*
import java.time.LocalDate
import java.time.LocalDateTime


sealed class Attendance {
    data class NotCompletedAttendance(val attendance: AttendanceList)
    data class CompletedAttendance(val attendance: AttendanceList)
}

sealed class AttendanceError {
    data class StudentNotInRegistry(val error: String = "Student is not in registry") : AttendanceError()
    data class LessonWasNotStarted(val error: String = "Lesson was not started"): AttendanceError()
}


data class AttendanceList(val className: ClassName,
                          val date: LocalDate,
                          val lessonHourNumber: LessonHourNumber,
                          val presentStudents: List<Student.PresentStudent> = emptyList(),
                          val absentStudents: List<Student.AbsentStudent> = emptyList(),
                          val lateStudents: List<Student.LateStudent> = emptyList())

// TODO include events needed to be published in the workflows
//class AttendanceCheckFinished
//class StudentNotedPresent
//class StudentNotedAbsent
//class StudentNotedLate

typealias CurrentTime = LocalDateTime

typealias IsLessonStarted = (LessonIdentifier) -> Boolean
typealias IsInRegistry = (Student, ClassRegistry) -> Boolean
typealias AreAllStudentsChecked = (AttendanceList, ClassRegistry) -> Boolean
typealias IsNotTooLate = (LessonStartTime, CurrentTime) -> Boolean

typealias NotePresence = (LessonIdentifier, Student.UncheckedStudent, AttendanceList, ClassRegistry) -> Either<AttendanceError, Attendance>
typealias NoteAbsence = (LessonIdentifier, Student.UncheckedStudent, AttendanceList, ClassRegistry) -> Either<AttendanceError, Attendance>
typealias NoteLate = (LessonIdentifier, Student.AbsentStudent, Attendance.CompletedAttendance) -> Attendance.CompletedAttendance
