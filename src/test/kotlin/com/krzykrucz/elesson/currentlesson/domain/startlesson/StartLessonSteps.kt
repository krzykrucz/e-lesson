package com.krzykrucz.elesson.currentlesson.domain.startlesson


import arrow.core.Either
import arrow.core.left
import arrow.core.orNull
import arrow.core.right
import com.krzykrucz.elesson.currentlesson.domain.getError
import com.krzykrucz.elesson.currentlesson.domain.getSuccess
import com.krzykrucz.elesson.currentlesson.domain.lessonHourNumberOf
import com.krzykrucz.elesson.currentlesson.domain.newClassName
import com.krzykrucz.elesson.currentlesson.domain.newStudent
import com.krzykrucz.elesson.currentlesson.domain.newTeacher
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.Teacher
import com.krzykrucz.elesson.currentlesson.domain.shared.isError
import com.krzykrucz.elesson.currentlesson.domain.shared.isSuccess
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertTrue

class StartLessonSteps : En {
    lateinit var teacher: Teacher
    var scheduledLessonProvider: FetchScheduledLesson = { _, _ -> TODO() }
    var classRegistryProvider: FetchClassRegistry = { TODO() }

    //    var checkLessonStarted: CheckLessonStarted = { false }
    lateinit var currentLessonOrError: Either<StartLessonError, StartedLesson>
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
                ScheduledLesson(
                    LocalDateTime.parse(time),
                    givenLessonHourNumber,
                    teacher,
                    newClassName(className),
                    LessonSubject(
                        NonEmptyText(
                            "Elixirs"
                        )
                    )
                ).right()
            }
        }
        Given("Class registry for class {word}") { className: String ->
            givenClassName = newClassName(className)
            classRegistryProvider = {
                ClassRegistry(
                    listOf(
                        newStudent(
                            "Harry",
                            "Potter",
                            1
                        )
                    ), it
                ).right()
            }
        }
        Given("Failed to check lesson schedule") {
            scheduledLessonProvider = { _, _ -> StartLessonError.NotScheduledLesson().left() }
        }
        Given("Failed to fetch class registry") {
            classRegistryProvider = { _ -> StartLessonError.ClassRegistryUnavailable().left() }
        }
//        Given("Lesson was already started") {
//            checkLessonStarted = { true }
//        }
        When("Lesson is started at {word}") { startTime: String ->
            attemptedStartTime = LocalDateTime.parse(startTime)
            val result = runBlocking {
                startLesson(
                    scheduledLessonProvider,
                    validateStartTime(),
                    classRegistryProvider
                )(
                    teacher,
                    attemptedStartTime
                )
            }
            currentLessonOrError = result
        }
        Then("Lesson before attendance should be started") {
            assertTrue(currentLessonOrError.isSuccess())

            val lessonBeforeAttendance = currentLessonOrError.getSuccess()

            assertThat(lessonBeforeAttendance.id.className, equalTo(givenClassName))
            assertThat(lessonBeforeAttendance.id.date, equalTo(givenDate))
            assertThat(lessonBeforeAttendance.id.lessonHourNumber, equalTo(givenLessonHourNumber))
            assertThat(lessonBeforeAttendance.clazz, equalTo(runBlocking { classRegistryProvider(givenClassName) }.orNull()))
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
