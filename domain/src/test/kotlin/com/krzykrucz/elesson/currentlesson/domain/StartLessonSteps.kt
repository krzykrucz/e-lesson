package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.Option
import arrow.effects.IO
import io.cucumber.java8.En
import java.time.LocalDateTime
import kotlin.test.assertTrue

class StartLessonSteps : En {
    lateinit var teacher: Teacher
    var scheduledLessonProvider: CheckScheduledLesson = { teacher, localDateTime -> IO.never }
    var classRegistryProvider: FetchClassRegistry = { className -> IO.never }
    lateinit var output: Option<CurrentLesson>

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
                IO.just(scheduledLesson)
            }
        }
        Given("Class registry for class {word}") { className: String ->
            classRegistryProvider = {
                IO.just(ClassRegistry(listOf(newStudent("Harry", "Potter", 1)), it))
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
            assertTrue(output.nonEmpty())
        }
        Then("Lesson should not be started") {
            assertTrue(output.isEmpty())
        }
        Then("Lesson should be before attendance") {
//            TODO()
        }
    }

}