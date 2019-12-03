package com.krzykrucz.elesson.currentlesson.domain


import arrow.core.NonEmptyList
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.refined.RawText
import io.cucumber.java8.En
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals


class StartLessonSteps : En {

    lateinit var teacher: Teacher
    lateinit var time: String
    lateinit var lessonHourNumber: LessonHourNumber
    lateinit var outputLesson: StartedLesson
    lateinit var checkSchedule: CheckSchedule
    lateinit var fetchClassRegistry: FetchClassRegistry

    init {
        Given("Teacher {string}") { teacherName: String ->
            this.teacher = Teacher(NonEmptyText.of(teacherName).orNull()!!)
        }
        Given("Current time {word}") { time: String ->
            this.time = time
        }
        Given("Scheduled lesson for class {word}, lesson number {word} and date {word}") { className: String, hourNumber: String, date: String ->
            lessonHourNumber = LessonHourNumber.of(hourNumber.toInt()).orNull()!!
            checkSchedule = { _, lessonStartTime ->
                ScheduledLesson(
                    ClassName(NonEmptyText.of(className).orNull()!!),
                    ScheduledTime.of(LocalDate.parse(date), lessonHourNumber.time),
                    lessonHourNumber
                )
            }
        }
        Given("Class registry for class {word}") { className: String ->
            fetchClassRegistry = {
                ClassRegistry(it, NonEmptyList.just(
                    StudentRecord(
                        NonEmptyText.of("Harry").orNull()!!,
                        NonEmptyText.of("Potter").orNull()!!
                    )))
            }
        }
        When("Lesson is started") {
            outputLesson = startLesson(checkSchedule, fetchClassRegistry, teacher, AttemptedLessonStartTime(RawText(time)))
        }
        Then("Lesson before attendance should be started") {
            // don't modify this section
            assertEquals(outputLesson.startTime.dateTime, LocalDateTime.parse(time))
            assertEquals(outputLesson.teacher, teacher)
            assertEquals(outputLesson.hourNumber, lessonHourNumber)
            assertEquals(outputLesson.classRegistry.className.name.text, "Gryffindor")

            assertEquals(outputLesson.classRegistry.studentList.head.firstName.text, "Harry")
            assertEquals(outputLesson.classRegistry.studentList.head.secondName.text, "Potter")
        }
    }

}
