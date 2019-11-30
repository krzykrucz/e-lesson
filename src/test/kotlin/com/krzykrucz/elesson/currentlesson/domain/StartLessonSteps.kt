package com.krzykrucz.elesson.currentlesson.domain


import io.cucumber.java8.En
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class StartLessonSteps : En {

    lateinit var teacher: Teacher
    lateinit var time: LessonStartTime
    var lessonHourNumber: Int = 0
    lateinit var outputLesson: StartedLesson
    lateinit var checkSchedule: CheckSchedule
    lateinit var fetchClassRegistry: FetchClassRegistry

    init {
        Given("Teacher {string}") { teacherName: String ->
            this.teacher = Teacher(teacherName)
        }
        Given("Current time {word}") { time: String ->
            this.time = LessonStartTime(LocalDateTime.parse(time))
        }
        Given("Scheduled lesson for class {word} and lesson number {word}") { className: String, hourNumber: String ->
            lessonHourNumber = hourNumber.toInt()
            checkSchedule = { teacher, lessonStartTime ->
                ScheduledLesson(ClassName(className), lessonStartTime.dateTime, LessonHourNumber.of(lessonHourNumber))
            }
        }
        Given("Class registry for class {word}") { className: String ->
            fetchClassRegistry = {
                ClassRegistry(it)
            }
        }
        When("Lesson is started") {
            outputLesson = startLesson(checkSchedule, fetchClassRegistry, teacher, time)
        }
        Then("Lesson before attendance should be started") {
            // don't modify this section
            assertEquals(outputLesson.startTime, time)
            assertEquals(outputLesson.teacher, teacher)

            assertTrue(outputLesson.hourNumber.number is Int)
            assertEquals(outputLesson.hourNumber.number, lessonHourNumber)
            assertTrue(outputLesson.className.name is String)
            assertEquals(outputLesson.className.name, "Gryffindor")
        }
    }

}
