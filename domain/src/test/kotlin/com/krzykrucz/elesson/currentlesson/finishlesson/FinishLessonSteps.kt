package com.krzykrucz.elesson.currentlesson.finishlesson

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.CurrentTime
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.FinishLessonError
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.FinishedLesson
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.bellRang
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.finishLesson
import com.krzykrucz.elesson.currentlesson.lessonHourNumberOf
import com.krzykrucz.elesson.currentlesson.newClassName
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.TopicTitle
import io.cucumber.java8.En
import java.time.LocalDate
import kotlin.test.assertTrue

class FinishLessonSteps : En {

    private lateinit var currentTime: CurrentTime
    private lateinit var inProgressLesson: InProgressLesson
    private lateinit var finishedLesson: Either<FinishLessonError, FinishedLesson>
    private val anyLessonIdentifier: LessonIdentifier = LessonIdentifier(
        LocalDate.now(),
        lessonHourNumberOf(1),
        newClassName("Ravenclaw")
    )

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
            anyLessonIdentifier,
            LessonTopic(
                LessonOrdinalInSemester(
                    NaturalNumber.of(lessonNumber).orNull()!!
                ),
                TopicTitle(NonEmptyText("Quantum math")),
                LocalDate.now()
            )
        )
}
