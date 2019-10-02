package com.krzykrucz.elesson.currentlesson.domain.attendance

import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonStartTime
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime


class IsNotTooLateSteps : En {
    lateinit var lessonHourNumber: LessonHourNumber
    lateinit var currentTime: CurrentTime
    private val getLessonStartTime: GetLessonStartTime = { _ ->
        LessonStartTime(LocalDateTime.of(2019, 9, 9, 10, 30))
    }
    var isNotTooLate: Boolean = true

    init {
        Given("Lesson hour number is {word}") { lessonHour: String ->
            lessonHourNumber = LessonHourNumber(NaturalNumber.of(lessonHour.toInt())!!)
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