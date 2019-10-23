package com.krzykrucz.elesson.currentlesson.attendance.domain

import arrow.core.getOrElse
import arrow.core.maybe
import com.krzykrucz.elesson.currentlesson.shared.StudentRecord
import java.time.temporal.ChronoUnit

fun noteAbsence(
        isInRegistry: IsInRegistry,
        completeListIfAllStudentsChecked: CompleteListIfAllStudentsChecked,
        addAbsentStudent: AddAbsentStudent
): NoteAbsence = { uncheckedStudent, notCompletedAttendance, classRegistry ->
    isInRegistry(uncheckedStudent, classRegistry)
            .maybe {
                addAbsentStudent(notCompletedAttendance, uncheckedStudent)
            }.map {
                completeListIfAllStudentsChecked(it, classRegistry)
            }.toEither {
                AttendanceError.StudentNotInRegistry()
            }
}

fun notePresence(
        isInRegistry: IsInRegistry,
        completeListIfAllStudentsChecked: CompleteListIfAllStudentsChecked,
        addPresentStudent: AddPresentStudent
): NotePresence = { uncheckedStudent, notCompletedAttendance, classRegistry ->
    isInRegistry(uncheckedStudent, classRegistry)
            .maybe {
                addPresentStudent(notCompletedAttendance, uncheckedStudent)
            }.map {
                completeListIfAllStudentsChecked(it, classRegistry)
            }.toEither {
                AttendanceError.StudentNotInRegistry()
            }
}

fun noteLate(
        isNotTooLate: IsNotTooLate
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

fun isNotTooLate(getLessonStartTime: GetLessonStartTime): IsNotTooLate = { lessonHour, currentTime ->
    val lessonStartTime = getLessonStartTime(lessonHour)
    val timeDifference: Long = ChronoUnit.MINUTES.between(lessonStartTime, currentTime) // TODO refactor
    timeDifference <= 15
}

fun isInRegistry(): IsInRegistry = { student, classRegistry ->
    classRegistry.students.contains(StudentRecord(
            firstName = student.firstName,
            secondName = student.secondName,
            numberInRegister = student.numberInRegister
    ))
}

fun completeList(): CompleteListIfAllStudentsChecked = { attendanceList, classRegistry ->
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
    (absentStudentsRecords + presentStudentsRecords).containsAll(classRegistry.students)
            .maybe { CheckedAttendanceList(attendanceList.presentStudents, attendanceList.absentStudents) }
            .getOrElse { attendanceList }
}

fun addAbsentStudent(): AddAbsentStudent = { attendanceList, student ->
    attendanceList.copy(absentStudents = attendanceList.absentStudents + student.toAbsent())
}

fun addPresentStudent(): AddPresentStudent = { attendanceList, student ->
    attendanceList.copy(presentStudents = attendanceList.presentStudents + student.toPresent())
}

fun getLessonStartTime(): GetLessonStartTime = { lessonHourNumber ->
    lessonHourNumber.getLessonScheduledStartTime()
}