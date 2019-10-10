package com.krzykrucz.elesson.currentlesson.domain.startlesson

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


data class NumberInRegister(val number: NaturalNumber)
data class FirstName(val name: NonEmptyText)
data class SecondName(val name: NonEmptyText)

data class ClassName(val name: NonEmptyText)

data class StudentRecord(val firstName: FirstName,
                         val secondName: SecondName,
                         val numberInRegister: NumberInRegister)

typealias StudentList = List<StudentRecord>

data class ClassRegistry(val students: StudentList,
                         val className: ClassName)

data class Teacher(val firstName: FirstName,
                   val secondName: SecondName)

typealias ScheduledLessonStartTime = LocalDateTime

data class ScheduledLesson(val scheduledTime: ScheduledLessonStartTime,
                           val lessonHourNumber: LessonHourNumber,
                           val teacher: Teacher,
                           val className: ClassName)


data class LessonHourNumber private constructor(val number: NaturalNumber) {
    companion object {
        private val SCHEDULE: Map<NaturalNumber, LocalTime> = mapOf(
                NaturalNumber.ONE to LocalTime.parse("10:00:00"),
                NaturalNumber.TWO to LocalTime.parse("10:55:00"),
                NaturalNumber.THREE to LocalTime.parse("11:45:00"),
                NaturalNumber.FOUR to LocalTime.parse("13:00:00"),
                NaturalNumber.FIVE to LocalTime.parse("13:50:00"),
                NaturalNumber.SIX to LocalTime.parse("14:40:00"),
                NaturalNumber.SEVEN to LocalTime.parse("15:30:00"),
                NaturalNumber.EIGHT to LocalTime.parse("16:25:00")
        )

        fun of(num: NaturalNumber): Option<LessonHourNumber> =
                if (SCHEDULE.containsKey(num)) {
                    Some(LessonHourNumber(num))
                } else {
                    None
                }
    }

    // Exception won't be thrown because creation of LessonHourNumber is validated
    fun getLessonScheduledStartTime(): LocalTime = SCHEDULE[this.number]!!
}

data class LessonStartTime(val time: LocalDateTime)
data class StartedLesson(val id: LessonIdentifier,
                         val clazz: ClassRegistry)

data class LessonIdentifier(val date: LocalDate,
                            val lessonHourNumber: LessonHourNumber,
                            val className: ClassName)

sealed class StartLessonError {
    data class NotScheduledLesson(val error: String = "Cannot start a lesson outside of a lesson hour for which it's scheduled") : StartLessonError()
    data class ClassRegistryUnavailable(val error: String = "Class registry unavailable") : StartLessonError()
    data class LessonAlreadyStarted(val error: String = "Lesson already started") : StartLessonError()
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

