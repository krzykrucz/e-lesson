package com.krzykrucz.elesson.currentlesson.domain.attendance

import com.krzykrucz.elesson.currentlesson.domain.shared.LessonHourNumber
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime

// FIXME move these test cases entirely to check attendance spec (we should only test pipeline, not specific functions)
class IsNotTooLateSteps : En {
    lateinit var lessonHourNumber: LessonHourNumber
    lateinit var currentTime: CurrentTime
    private val getLessonStartTime: GetLessonStartTime =
        getLessonStartTime()
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