package com.krzykrucz.elesson.currentlesson.domain


import com.virtuslab.basetypes.refined.NonEmptyText
import io.cucumber.java8.En
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class StartLessonSteps : En {

    lateinit var teacher: String
    lateinit var time: String
    var lessonHourNumber: Int = 0
    lateinit var outputLesson: StartedLesson
    lateinit var checkSchedule: CheckSchedule
    lateinit var fetchClassRegistry: FetchClassRegistry
    var exception: Exception? = null

    init {
        Given("Teacher {string}") { teacherName: String ->
            this.teacher = teacherName
        }
        Given("Current time {word}") { time: String ->
            this.time = time
        }
        Given("Scheduled lesson for class {word}, lesson number {word} and date {word}") { className: String, hourNumber: String, date: String ->
            lessonHourNumber = hourNumber.toInt()
            checkSchedule = { teacher, lessonStartTime ->
                val lessonHourNumber = LessonHourNumber.of(hourNumber.toInt())
                ScheduledLesson(
                    ClassName(NonEmptyText.of(className).orNull()!!),
                    ScheduledTime.of(LocalDate.parse(date), lessonHourNumber.time),
                    lessonHourNumber
                )
            }
        }
        Given("Class registry for class {word}") { className: String ->
            fetchClassRegistry = {
                ClassRegistry(it, listOf(
                    StudentRecord(
                        NonEmptyText.of("Harry").orNull()!!,
                        NonEmptyText.of("Potter").orNull()!!
                    )))
            }
        }
        Given("Empty class registry for class {word}") { className: String ->
            fetchClassRegistry = {
                ClassRegistry(it, emptyList())
            }
        }
        When("Lesson is started") {
            try {
                val teacher = Teacher(teacher)
                outputLesson = startLesson(checkSchedule, fetchClassRegistry, teacher, AttemptedLessonStartTime(LocalDateTime.parse(time)))
            } catch (ex: Exception) {
                exception = ex
            }
        }
        Then("Lesson before attendance should be started") {
            // don't modify this section
            assertEquals(outputLesson.startTime.dateTime, LocalDateTime.parse(time))
            assertEquals(outputLesson.teacher.name, teacher)
            assertEquals(outputLesson.hourNumber.number.number, lessonHourNumber)
            assertEquals(outputLesson.classRegistry.className.name.text, "Gryffindor")

            assertEquals(outputLesson.classRegistry.studentList.first().firstName.text, "Harry")
            assertEquals(outputLesson.classRegistry.studentList.first().secondName.text, "Potter")
        }
        Then("Lesson should not be started") {
            assertNotNull(exception, "Test should fail with exception")
        }
    }

}
