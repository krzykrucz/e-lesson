package com.krzykrucz.elesson.currentlesson.startlesson.domain

import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.Teacher
import java.time.LocalDateTime

typealias ScheduledLessonStartTime = LocalDateTime

data class ScheduledLesson(val scheduledTime: ScheduledLessonStartTime,
                           val lessonHourNumber: LessonHourNumber,
                           val teacher: Teacher,
                           val className: ClassName)

data class StartedLesson(val id: LessonIdentifier,
                         val clazz: ClassRegistry)

sealed class StartLessonError {
    data class NotScheduledLesson(val error: String = "Cannot start a lesson outside of a lesson hour for which it's scheduled") : StartLessonError()
    data class ClassRegistryUnavailable(val error: String = "Class registry unavailable") : StartLessonError()
}

//data class AbsentStudent(val student: Student)
//
//sealed class PresentStudent
//data class PreparedStudent(val student: Student) : PresentStudent()
//data class UnpreparedStudent(val student: Student) : PresentStudent()
//
//data class LateStudent(val student: PresentStudent)
//
//
//data class TopicOrdinal(val number: NaturalNumber)
//
//data class LessonTopic(val ordinal: TopicOrdinal,
//                       val title: NonEmptyText,
//                       val date: LocalDate)
//
//enum class Presence {
//    Present,
//    Absent,
//    Late
//}
//data class Attendance(val date: LocalDate,
//                      val lessonHourNumber: LessonHourNumber,
//                      val presentStudents: List<PresentStudent>,
//                      val absentStudents: List<AbsentStudent>)
//data class LessonBeforeTopic(val id: LessonIdentifier,
//                             val attendance: Attendance,
//                             val clazz: ClassRegistry) : CurrentLesson()
//
//data class LessonIntroduction(val id: LessonIdentifier,
//                              val attendance: Attendance,
//                              val topic: LessonTopic) : CurrentLesson()
//
//data class InProgressLesson(val id: LessonIdentifier,
//                            val attendance: Attendance,
//                            val topic: LessonTopic) : CurrentLesson()
//
//
//data class FinishedLesson(val id: LessonIdentifier,
//                          val topic: LessonTopic)
