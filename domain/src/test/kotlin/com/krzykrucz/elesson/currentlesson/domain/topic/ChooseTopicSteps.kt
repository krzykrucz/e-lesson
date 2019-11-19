package com.krzykrucz.elesson.currentlesson.domain.topic


import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.TopicTitle
import com.krzykrucz.elesson.currentlesson.getSuccess
import com.krzykrucz.elesson.currentlesson.lessonHourNumberOf
import com.krzykrucz.elesson.currentlesson.newClassName
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate

class ChooseTopicSteps : En {
    lateinit var topicTitle: TopicTitle
    lateinit var inProgressLesson: Either<ChooseTopicError, InProgressLesson>
    lateinit var finishedLessonsCount: FinishedLessonsCount
    var isAttendanceChecked: Boolean = true
    private val today = LocalDate.now()
    private val lessonIdentifier = LessonIdentifier(
        today,
        lessonHourNumberOf(1),
        newClassName("Hufflepuff")
    )

    init {
        Given("Topic title") {
            topicTitle = TopicTitle(
                NonEmptyText("Unforgivable curses")
            )
        }
        Given("Checked Attendance") {
            isAttendanceChecked = true
        }
        And("Finished Lessons Count") {
            finishedLessonsCount = FinishedLessonsCount(4)
        }
        And("Attendance is not checked") {
            isAttendanceChecked = false
        }
        When("Choosing a topic") {
            inProgressLesson = chooseTopic()(isAttendanceChecked, topicTitle, finishedLessonsCount, lessonIdentifier)
        }
        Then("Lesson is in progress") {
            assertThat(inProgressLesson.getSuccess()).isEqualToComparingFieldByField(
                InProgressLesson(
                    lessonIdentifier,
                    LessonTopic(
                        LessonOrdinalInSemester(
                            NaturalNumber.FIVE
                        ), topicTitle, today
                    )
                )
            )
        }
        Then("Choose topic error is returned") {
            assertThat(inProgressLesson.isLeft()).isTrue()
        }
    }
}
