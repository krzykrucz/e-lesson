package com.krzykrucz.elesson.currentlesson.attendance.domain

import arrow.core.left
import arrow.core.right
import com.krzykrucz.elesson.currentlesson.shared.StudentRecord
import java.time.temporal.ChronoUnit

fun noteAbsence(
        isInRegistry: IsInRegistry,
        areAllStudentsChecked: AreAllStudentsChecked
): NoteAbsence = { uncheckedStudent, notCompletedAttendance ->
    val classRegistry = notCompletedAttendance.classRegistry
    if (isInRegistry(uncheckedStudent, classRegistry)) {
        val updatedAttendanceList = notCompletedAttendance.attendance.addAbsentStudent(uncheckedStudent)
        if (areAllStudentsChecked(updatedAttendanceList, classRegistry)) {
            CheckedAttendance(updatedAttendanceList, classRegistry).right()
        } else {
            NotCompletedAttendance(updatedAttendanceList, classRegistry).right()
        }
    } else {
        AttendanceError.StudentNotInRegistry().left()
    }
}

fun notePresence(
        isInRegistry: IsInRegistry,
        areAllStudentsChecked: AreAllStudentsChecked
): NotePresence = { uncheckedStudent, notCompletedAttendance ->
    val classRegistry = notCompletedAttendance.classRegistry
    if (isInRegistry(uncheckedStudent, classRegistry)) {
        val updatedAttendanceList = notCompletedAttendance.attendance.addPresentStudent(uncheckedStudent)
        if (areAllStudentsChecked(updatedAttendanceList, classRegistry)) {
            CheckedAttendance(updatedAttendanceList, classRegistry).right()
        } else {
            NotCompletedAttendance(updatedAttendanceList, classRegistry).right()
        }
    } else {
        AttendanceError.StudentNotInRegistry().left()
    }
}

fun noteLate(
        isNotTooLate: IsNotTooLate
): NoteLate = { lessonId, absentStudent, checkedAttendance, currentTime ->
    if (isNotTooLate(lessonId.lessonHourNumber, currentTime)) {
        val attendance = checkedAttendance.attendance
        val updatedAbsentStudents = attendance.absentStudents.minusElement(absentStudent)
        val updatedPresentStudents = attendance.presentStudents.plusElement(absentStudent.toPresent())
        val updatedAttendance = attendance.copy(
                absentStudents = updatedAbsentStudents,
                presentStudents = updatedPresentStudents
        )
        checkedAttendance.copy(
                attendance = updatedAttendance
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

private fun AttendanceList.addAbsentStudent(student: UncheckedStudent): AttendanceList =
        this.copy(absentStudents = this.absentStudents.plusElement(student.toAbsent()))


private fun AttendanceList.addPresentStudent(student: UncheckedStudent): AttendanceList =
        this.copy(presentStudents = this.presentStudents.plusElement(student.toPresent()))