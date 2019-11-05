package com.krzykrucz.elesson.currentlesson.finishlesson

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.*
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.topic.domain.InProgressLesson
import com.krzykrucz.elesson.currentlesson.topic.domain.LessonOrdinalNumber
import com.krzykrucz.elesson.currentlesson.topic.domain.LessonTopic
import com.krzykrucz.elesson.currentlesson.topic.domain.TopicTitle
import io.cucumber.java8.En
import java.time.LocalDate
import kotlin.test.assertTrue

class FinishLessonSteps : En {

    private lateinit var currentTime: CurrentTime
    private lateinit var inProgressLesson: InProgressLesson
    private lateinit var finishedLesson: Either<FinishLessonError, FinishedLesson>

    init {

        Given("Current time of {currentTime}") { currentTimeOf: CurrentTime ->
            currentTime = currentTimeOf
        }

        And("In progress lesson number {int}") { lessonNumber: Int ->
            inProgressLesson = inProgressLessonOfNumber(lessonNumber)
        }

        When("Finishing a lesson") {
            finishedLesson = finishLesson(bellRang())(
                inProgressLesson,
                currentTime
            )
        }

        Then("Lesson is {finished}") { finishedLessonResult: MatchFinishLessonResult ->
            assertTrue { finishedLessonResult.matches(inProgressLesson, finishedLesson) }
        }
    }

    private fun inProgressLessonOfNumber(lessonNumber: Int) =
        InProgressLesson(
            LessonTopic(
                LessonOrdinalNumber(
                    NaturalNumber.of(lessonNumber).orNull()!!
                ),
                TopicTitle(NonEmptyText("Quantum math")),
                LocalDate.now()
            )
        )
}