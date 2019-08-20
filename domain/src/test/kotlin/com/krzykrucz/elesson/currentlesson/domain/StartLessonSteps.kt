package com.krzykrucz.elesson.currentlesson.domain

import arrow.effects.IO
import io.cucumber.java8.En
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertTrue

class StartLessonSteps : En {
    lateinit var teacher: Teacher
    var scheduledLessonProvider: CheckScheduledLesson = { teacher, localDateTime -> IO.never }
    var classRegistryProvider: FetchClassRegistry = { className -> IO.never }
    var checkLessonStarted: CheckLessonStarted = { false }
    lateinit var currentLessonOrError: Output<LessonBeforeAttendance, StartLessonError>
    lateinit var givenClassName: ClassName
    lateinit var givenDate: LocalDate
    private val givenLessonHourNumber = lessonHourNumberOf(1)

    init {
        Given("Some teacher") {
            teacher = newTeacher("Severus", "Snape")
        }
        Given("Scheduled lesson for class {word} and {word}") { className: String, time: String ->
            givenDate = LocalDateTime.parse(time).toLocalDate()
            scheduledLessonProvider = { teacher, localDateTime ->
                val scheduledTime = LocalDateTime.parse(time)
                val className1 = newClassName(className)
                val teacherCalendar = TeacherCalendar(emptyList())
                val scheduledLesson = ScheduledLesson(scheduledTime, givenLessonHourNumber, teacher, className1, teacherCalendar)
                AsyncFactory.justSuccess(scheduledLesson)
            }
        }
        Given("Class registry for class {word}") { className: String ->
            givenClassName = newClassName(className)
            classRegistryProvider = {
                AsyncFactory.justSuccess(ClassRegistry(listOf(newStudent("Harry", "Potter", 1)), it))
            }
        }
        Given("Failed to check lesson schedule") {
            scheduledLessonProvider = { _, _ -> AsyncFactory.justError(RuntimeException()) }
        }
        Given("Failed to fetch class registry") {
            classRegistryProvider = { _ -> AsyncFactory.justError(RuntimeException()) }
        }
        Given("Lesson was already started") {
            checkLessonStarted = { true }
        }
        When("Lesson is started at {word}") { startTime: String ->
            val result =
                    startLesson(
                            checkLessonStarted,
                            scheduledLessonProvider,
                            classRegistryProvider,
                            teacher,
                            LocalDateTime.parse(startTime)
                    )
            currentLessonOrError = result.evaluate()
        }
        Then("Lesson before attendance should be started") {
            assertTrue(currentLessonOrError.isSuccess())

            val lessonBeforeAttendance = currentLessonOrError.getSuccess()

            assertThat(lessonBeforeAttendance.id.className, equalTo(givenClassName))
            assertThat(lessonBeforeAttendance.id.date, equalTo(givenDate))
            assertThat(lessonBeforeAttendance.id.lessonHourNumber, equalTo(givenLessonHourNumber))
        }
        Then("Lesson should not be started because no scheduled lesson") {
            assertTrue { this.currentLessonOrError.isError() }
            val error = currentLessonOrError.getError()
            assertThat(error, instanceOf(StartLessonError.NotScheduledLesson::class.java))
        }
        Then("Lesson should not be started because class registry unavailable") {
            assertTrue { this.currentLessonOrError.isError() }
            val error = currentLessonOrError.getError()
            assertThat(error, instanceOf(StartLessonError.ClassRegistryUnavailable::class.java))
        }
        Then("Lesson should not be started because it's already started") {
            assertTrue { this.currentLessonOrError.isError() }
            val error = currentLessonOrError.getError()
            assertThat(error, instanceOf(StartLessonError.LessonAlreadyStarted::class.java))
        }
    }

}