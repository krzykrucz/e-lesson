package com.krzykrucz.elesson.currentlesson.domain.attendance

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.startlesson.Student
import java.time.LocalDate


data class UncheckedStudent(val student: Student)

data class AbsentStudent(val student: Student)

data class PresentStudent(val student: Student)

data class CompletedAttendance(val attendance: AttendanceList)

data class AttendanceList(val className: ClassName,
                          val date: LocalDate,
                          val lessonHourNumber: LessonHourNumber,
                          val presentStudents: List<PresentStudent> = emptyList(),
                          val absentStudents: List<AbsentStudent> = emptyList())

// TODO include events needed to be published in the workflows
//class AttendanceCheckFinished
//class StudentNotedPresent
//class StudentNotedAbsent
//class StudentNotedLate

typealias IsLessonStarted = (LessonIdentifier) -> Boolean
typealias IsInRegistry = (Student, ClassRegistry) -> Boolean

typealias NotePresence = (UncheckedStudent, AttendanceList, ClassRegistry) -> Either<AttendanceList, CompletedAttendance>
typealias NoteAbsence = (UncheckedStudent, AttendanceList, ClassRegistry) -> Either<AttendanceList, CompletedAttendance>
typealias NoteLate = (AbsentStudent, CompletedAttendance, ClassRegistry) -> CompletedAttendance