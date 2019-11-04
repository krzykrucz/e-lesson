package com.krzykrucz.elesson.currentlesson.shared

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import java.time.LocalDate
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


data class LessonIdentifier(val date: LocalDate,
                            val lessonHourNumber: LessonHourNumber,
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

        fun of(num: Number): Option<LessonHourNumber> =
            NaturalNumber.of(num.toInt())
                .flatMap(Companion::of)
    }

    // Exception won't be thrown because creation of LessonHourNumber is validated
    fun getLessonScheduledStartTime(): LocalTime = SCHEDULE[this.number]!!
}

sealed class LessonHourNumberError(msg: String) : RuntimeException(msg) {
    object LessonNumberNotInSchedule : LessonHourNumberError("Lesson hour number is not in schedule")
}

fun Option<LessonHourNumber>.toEither(): Either<Throwable, LessonHourNumber> =
    this.toEither { LessonHourNumberError.LessonNumberNotInSchedule }


data class Teacher(val firstName: FirstName,
                   val secondName: SecondName)

data class LessonOrdinalNumber(val number: NaturalNumber)
data class TopicTitle(val title: NonEmptyText)
data class LessonTopic(val lessonOrdinalNumber: LessonOrdinalNumber, val topicTitle: TopicTitle, val date: LocalDate)

sealed class Semester(val semesterOrdinalNumber: NaturalNumber)
object WinterSemester : Semester(NaturalNumber.ONE)
object SummerSemester : Semester(NaturalNumber.TWO)


data class LessonSubject(val subject: NonEmptyText)

sealed class LessonStatus(val status: String)
object Scheduled : LessonStatus("Scheduled")
object InProgress : LessonStatus("In Progress")
object Finished : LessonStatus("Finished")


