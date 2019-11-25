package com.krzykrucz.elesson.currentlesson.domain.startlesson


import com.krzykrucz.elesson.currentlesson.domain.startLesson
import io.cucumber.java8.En
import kotlin.reflect.KFunction2
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StartLessonSteps : En {

    lateinit var teacher
    lateinit var time
    lateinit var outputLesson

    init {
        Given("Teacher {string}") { teacherName: String ->
            TODO()
        }
        Given("Current time {word}") { time: String ->
            TODO()
        }
        Given("Scheduled lesson for class {word} and {word}") { className: String, time: String ->
            // leave empty for now
        }
        Given("Class registry for class {word}") { className: String ->
            // leave empty for now
        }
        When("Lesson is started at {word}") { startTime: String ->
            outputLesson = TODO()
        }
        Then("Lesson before attendance should be started") {
            // don't modify this section
            assertEquals(outputLesson.startTime, time)
            assertEquals(outputLesson.teacher, teacher)

            assertFalse { time is String }
            assertFalse { teacher is String }
            assertFalse { outputLesson is String }
            assertTrue { startLesson is KFunction2<*, *, *> }
        }
    }

}
