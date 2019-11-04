package com.krzykrucz.elesson.currentlesson.attendance.domain

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.*
import java.time.LocalDateTime
import java.time.LocalTime


sealed class Student {
    abstract val firstName: FirstName
    abstract val secondName: SecondName
    abstract val numberInRegister: NumberInRegister
}

data class UncheckedStudent(override val firstName: FirstName, override val secondName: SecondName, override val numberInRegister: NumberInRegister) : Student()
data class AbsentStudent(override val firstName: FirstName, override val secondName: SecondName, override val numberInRegister: NumberInRegister) : Student()
data class PresentStudent(override val firstName: FirstName, override val secondName: SecondName, override val numberInRegister: NumberInRegister) : Student()

fun UncheckedStudent.toAbsent(): AbsentStudent = AbsentStudent(this.firstName, this.secondName, this.numberInRegister)
fun UncheckedStudent.toPresent(): PresentStudent = PresentStudent(this.firstName, this.secondName, this.numberInRegister)
fun AbsentStudent.toPresent(): PresentStudent = PresentStudent(this.firstName, this.secondName, this.numberInRegister)

sealed class Attendance

data class IncompleteAttendanceList(
        val presentStudents: List<PresentStudent> = emptyList(),
        val absentStudents: List<AbsentStudent> = emptyList()
) : Attendance()

data class CheckedAttendanceList(
        val presentStudents: List<PresentStudent>,
        val absentStudents: List<AbsentStudent>
) : Attendance()


sealed class AttendanceError {
    data class StudentNotInRegistry(val error: String = "Student is not in registry") : AttendanceError()
    data class LessonNotFound(val error: String = "Lesson not found") : AttendanceError()
}

// TODO include events needed to be published in the workflows
//class AttendanceCheckFinished
//class StudentNotedPresent
//class StudentNotedAbsent
//class StudentNotedLate

typealias CurrentTime = LocalDateTime
typealias LessonTime = LocalTime

typealias IsInRegistry = (Student, ClassRegistry) -> Boolean
typealias GetLessonStartTime = (LessonHourNumber) -> LessonTime
typealias IsNotTooLate = (LessonHourNumber, CurrentTime) -> Boolean

typealias AddAbsentStudent = (IncompleteAttendanceList, UncheckedStudent) -> IncompleteAttendanceList
typealias AddPresentStudent = (IncompleteAttendanceList, UncheckedStudent) -> IncompleteAttendanceList

typealias CompleteListIfAllStudentsChecked = (IncompleteAttendanceList, ClassRegistry) -> Attendance

typealias NotePresence = (UncheckedStudent, IncompleteAttendanceList, ClassRegistry) -> Either<AttendanceError, Attendance>
typealias NoteAbsence = (UncheckedStudent, IncompleteAttendanceList, ClassRegistry) -> Either<AttendanceError, Attendance>
typealias NoteLate = (LessonHourNumber, AbsentStudent, CheckedAttendanceList, CurrentTime) -> CheckedAttendanceList

