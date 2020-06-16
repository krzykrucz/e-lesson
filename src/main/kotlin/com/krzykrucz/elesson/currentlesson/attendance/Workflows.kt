package com.krzykrucz.elesson.currentlesson.attendance

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.maybe
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.StudentRecord
import java.time.temporal.ChronoUnit


private typealias IsNotTooLate = (LessonHourNumber, CurrentTime) -> Boolean

private fun isNotTooLate(getLessonStartTime: GetLessonStartTime): IsNotTooLate = { lessonHour, currentTime ->
    val lessonStartTime = getLessonStartTime(lessonHour)
    val timeDifference: Long = ChronoUnit.MINUTES.between(lessonStartTime, currentTime) // TODO refactor
    timeDifference <= 15
}

private typealias IsInRegistry = (Student, ClassRegistry) -> Boolean

private val isInRegistry: IsInRegistry = { student, classRegistry ->
    classRegistry.students.contains(
        StudentRecord(
            firstName = student.firstName,
            secondName = student.secondName,
            numberInRegister = student.numberInRegister
        )
    )
}

private typealias CompleteListIfAllStudentsChecked = (IncompleteAttendanceList, ClassRegistry) -> Attendance

private val completeList: CompleteListIfAllStudentsChecked = { attendanceList, classRegistry ->
    val absentStudentsRecords = attendanceList.absentStudents
        .map { student ->
            StudentRecord(
                firstName = student.firstName,
                secondName = student.secondName,
                numberInRegister = student.numberInRegister
            )
        }
    val presentStudentsRecords = attendanceList.presentStudents
        .map { student ->
            StudentRecord(
                firstName = student.firstName,
                secondName = student.secondName,
                numberInRegister = student.numberInRegister
            )
        }
    val checkedStudents = (absentStudentsRecords + presentStudentsRecords)
    val students = classRegistry.students
    val containsAll = checkedStudents.containsAll(students)
    containsAll
        .maybe {
            CheckedAttendanceList(
                attendanceList.presentStudents,
                attendanceList.absentStudents
            )
        }
        .getOrElse { attendanceList }
}

private typealias AddAbsentStudent = (IncompleteAttendanceList, UncheckedStudent) -> IncompleteAttendanceList

private val addAbsentStudent: AddAbsentStudent = { attendanceList, student ->
    attendanceList.copy(absentStudents = attendanceList.absentStudents + student.toAbsent())
}

private typealias AddPresentStudent = (IncompleteAttendanceList, UncheckedStudent) -> IncompleteAttendanceList

private val addPresentStudent: AddPresentStudent = { attendanceList, student ->
    attendanceList.copy(presentStudents = attendanceList.presentStudents + student.toPresent())
}

private typealias GetLessonStartTime = (LessonHourNumber) -> LessonTime

private val getLessonStartTime: GetLessonStartTime = { lessonHourNumber ->
    lessonHourNumber.getLessonScheduledStartTime()
}

typealias NoteAbsence = (UncheckedStudent, IncompleteAttendanceList, ClassRegistry) -> Either<AttendanceError, Attendance>

fun noteAbsenceWorkflow(
    isStudentInRegistry: IsInRegistry = isInRegistry,
    completeListIfAllStudentsChecked: CompleteListIfAllStudentsChecked = completeList,
    addAbsentStudentToList: AddAbsentStudent = addAbsentStudent
): NoteAbsence = { uncheckedStudent, notCompletedAttendance, classRegistry ->
    isStudentInRegistry(uncheckedStudent, classRegistry)
        .maybe {
            addAbsentStudentToList(notCompletedAttendance, uncheckedStudent)
        }.map {
            completeListIfAllStudentsChecked(it, classRegistry)
        }.toEither {
            AttendanceError.StudentNotInRegistry()
        }
}

typealias NotePresence = (UncheckedStudent, IncompleteAttendanceList, ClassRegistry) -> Either<AttendanceError, Attendance>

fun notePresenceWorkflow(
    isStudentInRegistry: IsInRegistry = isInRegistry,
    completeListIfAllStudentsChecked: CompleteListIfAllStudentsChecked = completeList,
    addPresentStudentToList: AddPresentStudent = addPresentStudent
): NotePresence = { uncheckedStudent, notCompletedAttendance, classRegistry ->
    isStudentInRegistry(uncheckedStudent, classRegistry)
        .maybe {
            addPresentStudentToList(notCompletedAttendance, uncheckedStudent)
        }.map {
            completeListIfAllStudentsChecked(it, classRegistry)
        }.toEither {
            AttendanceError.StudentNotInRegistry()
        }
}

typealias NoteLate = (LessonHourNumber, AbsentStudent, CheckedAttendanceList, CurrentTime) -> CheckedAttendanceList

fun noteLateWorkflow(
    isNotTooLate: IsNotTooLate = isNotTooLate(getLessonStartTime)
): NoteLate = { lessonHourNumber, absentStudent, checkedAttendance, currentTime ->
    isNotTooLate(lessonHourNumber, currentTime)
        .maybe {
            checkedAttendance.copy(
                absentStudents = checkedAttendance.absentStudents - absentStudent,
                presentStudents = checkedAttendance.presentStudents + absentStudent.toPresent()
            )
        }.getOrElse {
            checkedAttendance
        }
}
