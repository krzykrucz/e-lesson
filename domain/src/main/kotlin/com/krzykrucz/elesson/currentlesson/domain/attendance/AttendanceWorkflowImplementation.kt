package com.krzykrucz.elesson.currentlesson.domain.attendance

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import java.time.LocalDateTime

fun noteAbsence(
        isLessonStarted: IsLessonStarted,
        isInRegistry: IsInRegistry,
        areAllStudentsChecked: AreAllStudentsChecked
): NoteAbsence = { lessonId, uncheckedStudent, attendanceList, classRegistry ->
    noteStudent(isLessonStarted, lessonId, isInRegistry, uncheckedStudent, classRegistry, attendanceList, areAllStudentsChecked, addAbsentStudent())
}

fun notePresence(
        isLessonStarted: IsLessonStarted,
        isInRegistry: IsInRegistry,
        areAllStudentsChecked: AreAllStudentsChecked
): NotePresence = { lessonId, uncheckedStudent, attendanceList, classRegistry ->
    noteStudent(isLessonStarted, lessonId, isInRegistry, uncheckedStudent, classRegistry, attendanceList, areAllStudentsChecked, addPresentStudent())
}

fun noteLate(
        isNotTooLate: IsNotTooLate
): NoteLate = { lessonId, absentStudent, completedAttendance ->
    if (isNotTooLate(lessonId.lessonStartTime, LocalDateTime.now())) {
        val attendance = completedAttendance.attendance
        val updatedAbsentStudents = attendance.absentStudents.minusElement(absentStudent)
        val updatedLateStudents = attendance.lateStudents.plusElement(absentStudent.toLate())
        val updatedAttendance = attendance.copy(
                absentStudents = updatedAbsentStudents,
                lateStudents = updatedLateStudents
        )
        completedAttendance.copy(
                attendance = updatedAttendance
        )
    } else {
        completedAttendance
    }
}

private fun <T: Student> noteStudent(
        isLessonStarted: IsLessonStarted,
        lessonId: LessonIdentifier,
        isInRegistry: IsInRegistry,
        student: T,
        classRegistry: ClassRegistry,
        attendanceList: AttendanceList,
        areAllStudentsChecked: AreAllStudentsChecked,
        updateAttendanceList: (AttendanceList, T) -> AttendanceList
): Either<AttendanceError, Attendance> =
        if (isLessonStarted(lessonId)) {
            if (isInRegistry(student, classRegistry)) {
                val updatedAttendanceList = updateAttendanceList(attendanceList, student)
                if (areAllStudentsChecked(updatedAttendanceList, classRegistry)) {
                    Attendance.CompletedAttendance(updatedAttendanceList).right() as Either<AttendanceError, Attendance>
                } else {
                    Attendance.NotCompletedAttendance(updatedAttendanceList).right() as Either<AttendanceError, Attendance>
                }
            } else {
                AttendanceError.StudentNotInRegistry().left()
            }
        } else {
            AttendanceError.LessonWasNotStarted().left()
        }

private fun addAbsentStudent(): (AttendanceList, Student.UncheckedStudent) -> AttendanceList = { attendanceList, uncheckedStudent ->
    attendanceList.copy(absentStudents = attendanceList.absentStudents.plusElement(uncheckedStudent.toAbsent()))
}

private fun addPresentStudent(): (AttendanceList, Student.UncheckedStudent) -> AttendanceList = { attendanceList, uncheckedStudent ->
    attendanceList.copy(presentStudents = attendanceList.presentStudents.plusElement(uncheckedStudent.toPresent()))
}
