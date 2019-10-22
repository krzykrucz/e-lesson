package com.krzykrucz.elesson.currentlesson.attendance.domain

import arrow.core.left
import arrow.core.right
import com.krzykrucz.elesson.currentlesson.shared.StudentRecord
import java.time.temporal.ChronoUnit

fun noteAbsence(
        isInRegistry: IsInRegistry,
        areAllStudentsChecked: AreAllStudentsChecked
): NoteAbsence = { uncheckedStudent, notCompletedAttendance, classRegistry ->
    if (isInRegistry(uncheckedStudent, classRegistry)) {
        val updatedAttendanceList = addAbsentStudent(notCompletedAttendance, uncheckedStudent)
        if (areAllStudentsChecked(updatedAttendanceList, classRegistry)) {
            CheckedAttendanceList(
                    updatedAttendanceList.presentStudents,
                    updatedAttendanceList.absentStudents
            ).right()
        } else {
            updatedAttendanceList.right()
        }
    } else {
        AttendanceError.StudentNotInRegistry().left()
    }
}

fun notePresence(
        isInRegistry: IsInRegistry,
        areAllStudentsChecked: AreAllStudentsChecked
): NotePresence = { uncheckedStudent, notCompletedAttendance, classRegistry ->
    if (isInRegistry(uncheckedStudent, classRegistry)) {
        val updatedAttendanceList = addPresentStudent(notCompletedAttendance, uncheckedStudent)
        if (areAllStudentsChecked(updatedAttendanceList, classRegistry)) {// TODO move all that logic to the areAll... fun; convert to workflow
            CheckedAttendanceList(
                    updatedAttendanceList.presentStudents,
                    updatedAttendanceList.absentStudents
            ).right()
        } else {
            updatedAttendanceList.right()
        }
    } else {
        AttendanceError.StudentNotInRegistry().left()
    }
}

fun noteLate(
        isNotTooLate: IsNotTooLate
): NoteLate = { lessonHourNumber, absentStudent, checkedAttendance, currentTime ->
    if (isNotTooLate(lessonHourNumber, currentTime)) {
        val updatedAbsentStudents = checkedAttendance.absentStudents - absentStudent
        val updatedPresentStudents = checkedAttendance.presentStudents + absentStudent.toPresent()
        checkedAttendance.copy(
                absentStudents = updatedAbsentStudents,
                presentStudents = updatedPresentStudents
        )
    } else {
        checkedAttendance
    }
}

fun isNotTooLate(getLessonStartTime: GetLessonStartTime): IsNotTooLate = { lessonHour, currentTime ->
    val lessonStartTime = getLessonStartTime(lessonHour)
    val timeDifference: Long = ChronoUnit.MINUTES.between(lessonStartTime, currentTime)
    timeDifference <= 15
}

fun isInRegistry(): IsInRegistry = { student, classRegistry ->
    classRegistry.students.contains(StudentRecord(
            firstName = student.firstName,
            secondName = student.secondName,
            numberInRegister = student.numberInRegister
    ))
}

fun areAllStudentsChecked(): AreAllStudentsChecked = { attendanceList, classRegistry ->
    val absentStudents = attendanceList.absentStudents
            .map { student ->
                StudentRecord(
                        firstName = student.firstName,
                        secondName = student.secondName,
                        numberInRegister = student.numberInRegister
                )
            }
    val presentStudents = attendanceList.presentStudents
            .map { student ->
                StudentRecord(
                        firstName = student.firstName,
                        secondName = student.secondName,
                        numberInRegister = student.numberInRegister
                )
            }

    (absentStudents + presentStudents).containsAll(classRegistry.students)
}


// TODO wrap 2 below in workflows
private fun addAbsentStudent(attendanceList: IncompleteAttendanceList, student: UncheckedStudent): IncompleteAttendanceList =
        attendanceList.copy(absentStudents = attendanceList.absentStudents + student.toAbsent())


private fun addPresentStudent(attendanceList: IncompleteAttendanceList, student: UncheckedStudent): IncompleteAttendanceList =
        attendanceList.copy(presentStudents = attendanceList.presentStudents + student.toPresent())