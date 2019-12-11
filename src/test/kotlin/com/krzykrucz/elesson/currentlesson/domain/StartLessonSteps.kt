package com.krzykrucz.elesson.currentlesson.domain


import arrow.core.NonEmptyList
import com.krzykrucz.elesson.currentlesson.test
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.arrow.AsyncResult
import com.virtuslab.basetypes.result.arrow.toAsync
import com.virtuslab.basetypes.result.toResult
import io.cucumber.java8.En
import java.time.LocalDate
import java.time.LocalDateTime


class StartLessonSteps : En {

    lateinit var teacher: Teacher
    lateinit var time: String
    lateinit var lessonHourNumber: LessonHourNumber
    lateinit var output: AsyncResult<LessonStartedEvent, StartLessonError>
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
                ).toResult().toAsync()
            }
        }
        Given("Class registry for class {word}") { className: String ->
            fetchClassRegistry = {
                ClassRegistry(it, NonEmptyList.just(
                    StudentRecord(
                        NonEmptyText.of("Harry").orNull()!!,
                        NonEmptyText.of("Potter").orNull()!!
                    ))).toResult().toAsync()
            }
        }
        Given("Failed to fetch class registry") {
            fetchClassRegistry = { _ -> Result.error(StartLessonError.ClassRegistryUnavailable).toAsync() }
        }
        When("Lesson is started") {
            output = startLesson(checkSchedule, fetchClassRegistry)(teacher, AttemptedLessonStartTime(LocalDateTime.parse(time)))
        }
        Then("Lesson before attendance should be started") {
            // don't modify this section
            output
                .test()
                .assertThatSuccess { it.startTime.dateTime  == LocalDateTime.parse(time) }
                .assertThatSuccess { it.teacher == teacher }
                .assertThatSuccess { it.hourNumber == lessonHourNumber }
                .assertThatSuccess { it.className.name.text == "Gryffindor" }
        }
        Then("Lesson should not be started because it's too late or too soon") {
            // don't modify this section
            output
                .test()
                .assertFailure(StartLessonError.StartingTooEarlyOrTooLate)
        }
        Then("Lesson should not be started because class registry unavailable") {
            // don't modify this section
            output
                .test()
                .assertFailure(StartLessonError.ClassRegistryUnavailable)
        }
    }

}
