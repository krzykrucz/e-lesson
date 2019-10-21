package com.krzykrucz.elesson.currentlesson.attendance.domain

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import java.time.LocalDate
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

sealed class Attendance { // TODO remove abstract if possible
    abstract val attendance: AttendanceList
    abstract val classRegistry: ClassRegistry
}

fun Attendance.lessonId() = LessonIdentifier(
        date = this.attendance.date,
        lessonHourNumber = this.attendance.lessonHourNumber,
        className = this.attendance.className
)

data class NotCompletedAttendance(
        override val attendance: AttendanceList,
        override val classRegistry: ClassRegistry
) : Attendance()

data class CheckedAttendance(
        override val attendance: AttendanceList,
        override val classRegistry: ClassRegistry
) : Attendance()


sealed class AttendanceError {
    data class StudentNotInRegistry(val error: String = "Student is not in registry") : AttendanceError()
}


data class AttendanceList(val className: ClassName,
                          val date: LocalDate,
                          val lessonHourNumber: LessonHourNumber,
                          val presentStudents: List<PresentStudent> = emptyList(),
                          val absentStudents: List<AbsentStudent> = emptyList()
)
// TODO include events needed to be published in the workflows
//class AttendanceCheckFinished
//class StudentNotedPresent
//class StudentNotedAbsent
//class StudentNotedLate

typealias CurrentTime = LocalDateTime
typealias LessonTime = LocalTime

typealias IsInRegistry = (Student, ClassRegistry) -> Boolean
typealias AreAllStudentsChecked = (AttendanceList, ClassRegistry) -> Boolean
typealias GetLessonStartTime = (LessonHourNumber) -> LessonTime
typealias IsNotTooLate = (LessonHourNumber, CurrentTime) -> Boolean

typealias NotePresence = (UncheckedStudent, NotCompletedAttendance) -> Either<AttendanceError, Attendance>
typealias NoteAbsence = (UncheckedStudent, NotCompletedAttendance) -> Either<AttendanceError, Attendance>
typealias NoteLate = (LessonIdentifier, AbsentStudent, CheckedAttendance, CurrentTime) -> CheckedAttendance