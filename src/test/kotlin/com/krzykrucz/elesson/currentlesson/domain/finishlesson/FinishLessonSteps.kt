package com.krzykrucz.elesson.currentlesson.domain.finishlesson

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.TopicTitle
import com.krzykrucz.elesson.currentlesson.domain.lessonHourNumberOf
import com.krzykrucz.elesson.currentlesson.domain.newClassName
import io.cucumber.java8.En
import java.time.LocalDate
import kotlin.test.assertTrue

class FinishLessonSteps : En {

    private lateinit var finishLessonTime: FinishLessonTime
    private lateinit var inProgressLesson: InProgressLesson
    private lateinit var finishedLesson: Either<FinishLessonError, FinishedLesson>
    private val anyLessonIdentifier: LessonIdentifier =
        LessonIdentifier(
            LocalDate.now(),
            lessonHourNumberOf(1),
            newClassName("Ravenclaw")
        )

    init {

        Given("Current time of {currentTime}") { finishLessonTimeOf: FinishLessonTime ->
            finishLessonTime = finishLessonTimeOf
        }

        And("In progress lesson number {int}") { lessonNumber: Int ->
            inProgressLesson = inProgressLessonOfNumber(lessonNumber)
        }

        When("Finishing a lesson") {
            finishedLesson = finishLesson(bellRang())(
                inProgressLesson,
                finishLessonTime
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
                TopicTitle(
                    NonEmptyText(
                        "Quantum math"
                    )
                ),
                LocalDate.now()
            )
        )
}
