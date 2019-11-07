package com.krzykrucz.elesson.currentlesson.startlesson

import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.evaluate
import com.krzykrucz.elesson.currentlesson.getError
import com.krzykrucz.elesson.currentlesson.getSuccess
import com.krzykrucz.elesson.currentlesson.lessonHourNumberOf
import com.krzykrucz.elesson.currentlesson.newClassName
import com.krzykrucz.elesson.currentlesson.newStudent
import com.krzykrucz.elesson.currentlesson.newTeacher
import com.krzykrucz.elesson.currentlesson.shared.AsyncFactory
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.Output
import com.krzykrucz.elesson.currentlesson.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.shared.Teacher
import com.krzykrucz.elesson.currentlesson.shared.isError
import com.krzykrucz.elesson.currentlesson.shared.isSuccess
import com.krzykrucz.elesson.currentlesson.startlesson.domain.CheckScheduledLesson
import com.krzykrucz.elesson.currentlesson.startlesson.domain.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.startlesson.domain.ScheduledLesson
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartLessonError
import com.krzykrucz.elesson.currentlesson.startlesson.domain.startLesson
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
    //    var checkLessonStarted: CheckLessonStarted = { false }
    lateinit var currentLessonOrError: Output<StartedLesson, StartLessonError>
    lateinit var givenClassName: ClassName
    lateinit var givenDate: LocalDate
    lateinit var attemptedStartTime: LocalDateTime
    private val givenLessonHourNumber = lessonHourNumberOf(1)

    init {
        Given("Some teacher") {
            teacher = newTeacher("Severus", "Snape")
        }
        Given("Scheduled lesson for class {word} and {word}") { className: String, time: String ->
            givenDate = LocalDateTime.parse(time).toLocalDate()
            scheduledLessonProvider = { teacher, _ ->
                AsyncFactory.justSuccess(
                        ScheduledLesson(
                                LocalDateTime.parse(time),
                                givenLessonHourNumber,
                                teacher,
                                newClassName(className),
                                LessonSubject(NonEmptyText("Elixirs"))))
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
//        Given("Lesson was already started") {
//            checkLessonStarted = { true }
//        }
        When("Lesson is started at {word}") { startTime: String ->
            attemptedStartTime = LocalDateTime.parse(startTime)
            val result =
                startLesson(/*checkLessonStarted, */scheduledLessonProvider, classRegistryProvider)(
                    teacher,
                    attemptedStartTime
                )
            currentLessonOrError = result.evaluate()
        }
        Then("Lesson before attendance should be started") {
            assertTrue(currentLessonOrError.isSuccess())

            val lessonBeforeAttendance = currentLessonOrError.getSuccess()

            assertThat(lessonBeforeAttendance.id.className, equalTo(givenClassName))
            assertThat(lessonBeforeAttendance.id.date, equalTo(givenDate))
            assertThat(lessonBeforeAttendance.id.lessonHourNumber, equalTo(givenLessonHourNumber))
            assertThat(lessonBeforeAttendance.clazz, equalTo(classRegistryProvider(givenClassName).evaluate().getSuccess()))
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
//        Then("Lesson should not be started because it's already started") {
//            assertTrue { this.currentLessonOrError.isError() }
//            val error = currentLessonOrError.getError()
//            assertThat(error, instanceOf(StartLessonError.LessonAlreadyStarted::class.java))
//        }
    }

}
