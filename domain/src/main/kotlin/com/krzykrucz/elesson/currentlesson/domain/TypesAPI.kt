package com.krzykrucz.elesson.currentlesson.domain

import java.time.LocalDate
import java.time.LocalDateTime


data class NumberInRegister(val number: NaturalNumber)
data class FirstName(val name: NonEmptyText)
data class SecondName(val name: NonEmptyText)

data class ClassName(val name: NonEmptyText)

data class Student(val firstName: FirstName,
                   val secondName: SecondName,
                   val numberInRegister: NumberInRegister)

data class ClassRegistry(val students: List<Student>,
                         val className: ClassName)

data class Note(val note: NonEmptyText)

data class TeacherCalendar(val notes: List<Note>)

data class Teacher(val firstName: FirstName,
                   val secondName: SecondName)

data class ScheduledLesson(val scheduledTime: LocalDateTime,
                           val lessonHourNumber: LessonHourNumber,
                           val teacher: Teacher,
                           val className: ClassName,
                           val teacherCalendar: TeacherCalendar)


data class AbsentStudent(val student: Student)

sealed class PresentStudent
data class PreparedStudent(val student: Student) : PresentStudent()
data class UnpreparedStudent(val student: Student) : PresentStudent()

data class LateStudent(val student: PresentStudent)


data class TopicOrdinal(val number: NaturalNumber)

data class LessonTopic(val ordinal: TopicOrdinal,
                       val title: NonEmptyText,
                       val date: LocalDate)

enum class Presence {
    Present,
    Absent,
    Late
}

data class LessonHourNumber(val number: NaturalNumber)

data class Attendance(val date: LocalDate,
                      val lessonHourNumber: LessonHourNumber,
                      val presentStudents: List<PresentStudent>,
                      val absentStudents: List<AbsentStudent>)


sealed class CurrentLesson

data class LessonBeforeAttendance(val id: LessonIdentifier,
                                  val clazz: ClassRegistry) : CurrentLesson()

data class LessonBeforeTopic(val id: LessonIdentifier,
                             val attendance: Attendance,
                             val clazz: ClassRegistry) : CurrentLesson()

data class LessonIntroduction(val id: LessonIdentifier,
                              val attendance: Attendance,
                              val topic: LessonTopic) : CurrentLesson()

data class InProgressLesson(val id: LessonIdentifier,
                            val attendance: Attendance,
                            val topic: LessonTopic) : CurrentLesson()


data class FinishedLesson(val id: LessonIdentifier,
                          val topic: LessonTopic)


data class LessonIdentifier(val date: LocalDate,
                            val lessonHourNumber: LessonHourNumber,
                            val className: ClassName)

sealed class LessonError {
    data class NotScheduledLesson(val error: String = "Cannot start a lesson outside of a lesson hour for which it's scheduled") : LessonError()
}