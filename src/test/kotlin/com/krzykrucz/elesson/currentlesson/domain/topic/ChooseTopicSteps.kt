package com.krzykrucz.elesson.currentlesson.domain.topic


import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.getSuccess
import com.krzykrucz.elesson.currentlesson.domain.lessonHourNumberOf
import com.krzykrucz.elesson.currentlesson.domain.newClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.TopicTitle
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate

class ChooseTopicSteps : En {
    lateinit var topicTitle: TopicTitle
    lateinit var inProgressLesson: Either<ChooseTopicError, InProgressLesson>
    lateinit var finishedLessonsCount: CountFinishedLessons
    var isAttendanceChecked: CheckIfAttendanceIsChecked = { true }
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
            isAttendanceChecked = { true }
        }
        And("Finished Lessons Count") {
            finishedLessonsCount = { FinishedLessonsCount(4) }
        }
        And("Attendance is not checked") {
            isAttendanceChecked = { false }
        }
        When("Choosing a topic") {
            inProgressLesson = runBlocking {
                chooseTopicWorkflow(isAttendanceChecked, finishedLessonsCount)(topicTitle, lessonIdentifier)
            }
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
