package com.krzykrucz.elesson.currentlesson.attendance

import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.attendance.domain.CurrentTime
import com.krzykrucz.elesson.currentlesson.attendance.domain.GetLessonStartTime
import com.krzykrucz.elesson.currentlesson.attendance.domain.isNotTooLate
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime
import java.time.LocalTime


class IsNotTooLateSteps : En {
    lateinit var lessonHourNumber: LessonHourNumber
    lateinit var currentTime: CurrentTime
    private val getLessonStartTime: GetLessonStartTime = { _ ->
        LocalTime.of(10, 30, 0)
    }
    var isNotTooLate: Boolean = true

    init {
        Given("Lesson hour number is {word}") { lessonHour: String ->
            lessonHourNumber = LessonHourNumber.of(lessonHour.toInt()).orNull()!!
        }
        And("Time is at {word}") { time: String ->
            currentTime = LocalDateTime.parse(time)
        }
        When("Checking if it is too late") {
            isNotTooLate = isNotTooLate(getLessonStartTime)(lessonHourNumber, currentTime)
        }
        Then("It's too late") {
            assertThat(isNotTooLate).isFalse()
        }
        Then("It's not too late") {
            assertThat(isNotTooLate).isTrue()
        }
    }
}