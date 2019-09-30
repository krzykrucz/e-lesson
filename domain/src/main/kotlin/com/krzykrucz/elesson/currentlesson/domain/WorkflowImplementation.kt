package com.krzykrucz.elesson.currentlesson.domain

import com.krzykrucz.elesson.currentlesson.domain.StartLessonError.*
import java.time.LocalDate


private fun ScheduledLesson.lessonIdentifier() =
        LessonIdentifier(this.scheduledTime.toLocalDate(), this.lessonHourNumber, this.className)

private fun ScheduledLesson.toCurrentLessonWithClass(classRegistry: ClassRegistry, attemptedLessonStartTime: AttemptedLessonStartTime) =
        LessonBeforeAttendance(this.lessonIdentifier(), LessonStartTime(attemptedLessonStartTime), classRegistry)


fun startLesson(checkLessonStarted: CheckLessonStarted,
                checkScheduledLesson: CheckScheduledLesson,
                fetchClassRegistry: FetchClassRegistry): StartLesson = { teacher, attemptedStartTime ->
    checkScheduledLesson(teacher, attemptedStartTime)
        .mapError { _ -> NotScheduledLesson() }
        .failIf({ scheduledLesson -> attemptedStartTime.isBefore(scheduledLesson.scheduledTime) }, NotScheduledLesson())
        .failIf({ scheduledLesson -> attemptedStartTime.isAfter(scheduledLesson.scheduledTime.plusMinutes(44)) }, NotScheduledLesson())
        // ^ TODO maybe enclose these 2 mapping in the above and introduce another internal error type (lesson started too early or so)
        // ^ TODO extract a separate function with invariant
        .flatMapSuccess { scheduledLesson ->
            fetchClassRegistry(scheduledLesson.className)
                .mapSuccess { classRegistry -> scheduledLesson.toCurrentLessonWithClass(classRegistry, attemptedStartTime) }
                .mapError { _ -> ClassRegistryUnavailable() }
        }
        .failIf({ lesson -> checkLessonStarted(lesson.id) }, LessonAlreadyStarted())
}

fun checkAttendance(noteStudentPresence: NoteStudentPresence, checkIfAllStudentsAreNoted: CheckIfAllStudentsAreNoted): CheckAttendance = { lesssonBeforeAttendence ->
    val students = lesssonBeforeAttendence.clazz.students
    val attendanceFinished: AsyncOutput<AttendanceCheckFinished, StartLessonError> = students.mapAsync(noteStudentPresence)
            .map { studentsWithCheckedPresence ->
                val tuple = studentsWithCheckedPresence.partition {
                    it is AbsentStudent
                }
                tuple.first.map { it as AbsentStudent } to tuple.second.map { it as PresentStudent }
            }
            .map { (absentStudents, presentStudents) ->
                Attendance(
                        date = LocalDate.now(),
                        absentStudents = absentStudents,
                        presentStudents = presentStudents,
                        lessonHourNumber = lesssonBeforeAttendence.id.lessonHourNumber
                )
            }.map { attendance ->
                checkIfAllStudentsAreNoted(students, attendance)
            }

    attendanceFinished.mapSuccess { attendanceCheckFinished ->
        LessonBeforeTopic(
                id = lesssonBeforeAttendence.id,
                attendance = attendanceCheckFinished.attendance,
                clazz = lesssonBeforeAttendence.clazz
        )
    }

}




