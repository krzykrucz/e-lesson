package com.krzykrucz.elesson.currentlesson.domain


import io.cucumber.java8.En
import java.time.LocalDateTime
import kotlin.jvm.internal.Lambda
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class StartLessonSteps : En {

    lateinit var teacher: Teacher
    lateinit var time: LessonStartTime
    lateinit var outputLesson: StartedLesson
    lateinit var checkSchedule: CheckSchedule
    lateinit var fetchClassRegistry: FetchClassRegistry
    lateinit var className: String

    init {
        Given("Teacher {string}") { teacherName: String ->
            this.teacher = Teacher(teacherName)
        }
        Given("Current time {word}") { time: String ->
            this.time = LessonStartTime(LocalDateTime.parse(time))
        }
        Given("Scheduled lesson for class {word} and lesson number {word}") { className: String, hourNumber: String ->
            TODO()
        }
        Given("Class registry for class {word}") { className: String ->
            TODO()
        }
        When("Lesson is started") {
            outputLesson = startLesson(/*TODO*/teacher, time)
        }
        Then("Lesson before attendance should be started") {
            // don't modify this section
            assertEquals(outputLesson.startTime, time)
            assertEquals(outputLesson.teacher, teacher)

            assertTrue(outputLesson.className is ClassName)
            assertEquals(outputLesson.className.name, className)

            assertEquals((checkSchedule as Lambda<*>).arity, 2)
            assertEquals((fetchClassRegistry as Lambda<*>).arity, 1)
        }
    }

}
