package com.krzykrucz.elesson.currentlesson.domain


import io.cucumber.java8.En
import java.time.LocalDateTime
import kotlin.jvm.internal.Lambda
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class StartLessonSteps : En {

    lateinit var teacher: Teacher
    lateinit var time: LessonStartTime
    lateinit var outputLesson: StartedLesson

    init {
        Given("Teacher {string}") { teacherName: String ->
            this.teacher = Teacher(teacherName)
        }
        Given("Current time {word}") { time: String ->
            this.time = LessonStartTime(LocalDateTime.parse(time))
        }
        Given("Scheduled lesson for class {word} and {word}") { className: String, time: String ->
            // leave empty for now
        }
        Given("Class registry for class {word}") { className: String ->
            // leave empty for now
        }
        When("Lesson is started") {
            outputLesson = startLesson(teacher, time)
        }
        Then("Lesson before attendance should be started") {
            // don't modify this section
            assertEquals(outputLesson.startTime, time)
            assertEquals(outputLesson.teacher, teacher)

            assertFalse { time.javaClass == String::class.java }
            assertFalse { teacher.javaClass == String::class.java }
            assertFalse { outputLesson.javaClass == String::class.java }
            assertTrue { startLesson is Lambda<*> }
            assertEquals((startLesson as Lambda<*>).arity, 2)
        }
    }

}
