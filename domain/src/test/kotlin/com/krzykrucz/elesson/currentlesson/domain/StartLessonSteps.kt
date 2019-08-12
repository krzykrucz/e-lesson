package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.Either
import arrow.core.Option
import arrow.effects.IO
import arrow.effects.typeclasses.Duration
import io.cucumber.java8.En
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

class StartLessonSteps : En {
    lateinit var teacher: Teacher
    var scheduledLessonProvider: CheckScheduledLesson = { teacher, localDateTime -> IO.never }
    var classRegistryProvider: FetchClassRegistry = { className -> IO.never }
    lateinit var output: AsyncOutput<CurrentLesson>
    lateinit var lesson: CurrentLesson

    init {
        Given("Some teacher") {
            teacher = newTeacher("Severus", "Snape")
        }
        Given("Scheduled lesson for class {word} and {word}") { className: String, time: String ->
            scheduledLessonProvider = { teacher, localDateTime ->
                val scheduledTime = LocalDateTime.parse(time)
                val className1 = newClassName(className)
                val teacherCalendar = TeacherCalendar(emptyList())
                val scheduledLesson = ScheduledLesson(scheduledTime, lessonHourNumberOf(1), teacher, className1, teacherCalendar)
                asyncSuscess(scheduledLesson)
            }
        }
        Given("Class registry for class {word}") { className: String ->
            classRegistryProvider = {
                asyncSuscess(ClassRegistry(listOf(newStudent("Harry", "Potter", 1)), it))
            }
        }
        When("Lesson is started at {word}") { startTime: String ->
            output =
                    startLesson(
                            classRegistryProvider,
                            scheduledLessonProvider,
                            teacher,
                            LocalDateTime.parse(startTime)
                    )
        }
        Then("Lesson should be started") {
            val currentLessonOrError = output.evaluate()
            val lessonOption = currentLessonOrError.toOption()
            assertTrue { currentLessonOrError.isRight() }
        }
        Then("Lesson should not be started") {
            //            TODO()
        }
        Then("Lesson should be before attendance") {
            val currentLessonOrError = output.evaluate()
            assertTrue { currentLessonOrError is LessonBeforeAttendance }
            //            TODO()
        }
    }

}