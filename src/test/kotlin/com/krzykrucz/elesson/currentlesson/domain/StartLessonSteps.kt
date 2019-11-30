package com.krzykrucz.elesson.currentlesson.domain


import io.cucumber.java8.En
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class StartLessonSteps : En {

    lateinit var teacher: Teacher
    lateinit var time: LessonStartTime
    lateinit var lessonHourNumber: LessonHourNumber
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
            lessonHourNumber = LessonHourNumber.of(hourNumber.toInt())
            checkSchedule = { _, lessonStartTime ->
                ScheduledLesson(ClassName(className), lessonStartTime.dateTime, lessonHourNumber)
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

            assertTrue(outputLesson.hourNumber is LessonHourNumber)
            assertEquals(outputLesson.hourNumber, lessonHourNumber)
            assertTrue(outputLesson.className is ClassName)
            assertEquals(outputLesson.className.name, "Gryffindor")
        }
    }

}
