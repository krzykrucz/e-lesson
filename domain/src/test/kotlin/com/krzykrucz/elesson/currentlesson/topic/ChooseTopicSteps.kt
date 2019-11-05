package com.krzykrucz.elesson.currentlesson.topic


import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.getSuccess
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.topic.domain.ChooseTopicError
import com.krzykrucz.elesson.currentlesson.topic.domain.FinishedLessonsCount
import com.krzykrucz.elesson.currentlesson.topic.domain.LessonOrdinalNumber
import com.krzykrucz.elesson.currentlesson.topic.domain.LessonTopic
import com.krzykrucz.elesson.currentlesson.topic.domain.TopicTitle
import com.krzykrucz.elesson.currentlesson.topic.domain.chooseTopic
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate

class ChooseTopicSteps : En {
    lateinit var topicTitle: TopicTitle
    lateinit var inProgressLesson: Either<ChooseTopicError, InProgressLesson>
    lateinit var finishedLessonsCount: FinishedLessonsCount
    var isAttendanceChecked: Boolean = true
    private val now = LocalDate.now()

    init {
        Given("Topic title") {
            topicTitle = TopicTitle(NonEmptyText("Forbidden spells"))
        }
        Given("Checked Attendance") {
            isAttendanceChecked = true
        }
        And("Finished Lessons Count") {
            finishedLessonsCount = FinishedLessonsCount(NaturalNumber.FOUR)
        }
        And("Attendance is not checked") {
            isAttendanceChecked = false
        }
        When("Choosing a topic") {
            inProgressLesson = chooseTopic()(isAttendanceChecked, topicTitle, finishedLessonsCount, now)
        }
        Then("Lesson is in progress") {
            assertThat(inProgressLesson.getSuccess()).isEqualToComparingFieldByField(
                InProgressLesson(
                    LessonTopic(LessonOrdinalNumber(NaturalNumber.FIVE), topicTitle, now)
                )
            )
        }
        Then("Choose topic error is returned") {
            assertThat(inProgressLesson.isLeft()).isTrue()
        }
    }
}
