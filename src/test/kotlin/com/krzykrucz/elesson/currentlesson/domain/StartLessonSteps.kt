package com.krzykrucz.elesson.currentlesson.domain


import arrow.core.NonEmptyList
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.toResult
import io.cucumber.java8.En
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class StartLessonSteps : En {

    lateinit var teacher: Teacher
    lateinit var time: String
    lateinit var lessonHourNumber: LessonHourNumber
    lateinit var output: Result<StartedLesson, StartLessonError>
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
                )// TODO
            }
        }
        Given("Class registry for class {word}") { className: String ->
            fetchClassRegistry = {
                ClassRegistry(it, NonEmptyList.just(
                    StudentRecord(
                        NonEmptyText.of("Harry").orNull()!!,
                        NonEmptyText.of("Potter").orNull()!!
                    )))// TODO
            }
        }
        Given("Failed to fetch class registry") {
            // TODO
            fetchClassRegistry = { _ -> throw StartLessonError.ClassRegistryUnavailable }
        }
        When("Lesson is started") {
            output = startLesson(checkSchedule, fetchClassRegistry)(teacher, AttemptedLessonStartTime(LocalDateTime.parse(time)))
        }
        Then("Lesson before attendance should be started") {
            // don't modify this section
            assertTrue { output.isSuccess() }

            val outputLesson = output.component1()!!
            assertEquals(outputLesson.startTime.dateTime, LocalDateTime.parse(time))
            assertEquals(outputLesson.teacher, teacher)
            assertEquals(outputLesson.hourNumber, lessonHourNumber)
            assertEquals(outputLesson.classRegistry.className.name.text, "Gryffindor")
            assertEquals(outputLesson.classRegistry.studentList.head.firstName.text, "Harry")
            assertEquals(outputLesson.classRegistry.studentList.head.secondName.text, "Potter")
        }
        Then("Lesson should not be started because it's too late or too soon") {
            // don't modify this section
            assertTrue { output.isFailure() }

            val error = output.component2()!!
            assertTrue { error is StartLessonError.StartingTooEarlyOrTooLate }
        }
        Then("Lesson should not be started because class registry unavailable") {
            // don't modify this section
            assertTrue { output.isFailure() }

            val error = output.component2()!!
            assertTrue { error is StartLessonError.ClassRegistryUnavailable }
        }
    }

}
