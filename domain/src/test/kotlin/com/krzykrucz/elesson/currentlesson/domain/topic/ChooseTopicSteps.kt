package com.krzykrucz.elesson.currentlesson.domain.topic

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.*
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate

class ChooseTopicSteps : En {
    lateinit var topicTitle: TopicTitle
    lateinit var isAttendanceChecked: IsAttendanceChecked
    lateinit var inProgressLessonOrError: Either<ChooseTopicError, InProgressLesson>
    private val now = LocalDate.now()
    private val lessonId = LessonIdentifier(now, lessonHourNumberOf(1), newClassName("Slytherin"))

    init {
        Given("Topic title") {
            topicTitle = TopicTitle(NonEmptyText("Forbidden spells"))
        }
        And("Attendance is checked") {
            isAttendanceChecked = { _ -> true }
        }
        And("Attendance is not checked") {
            isAttendanceChecked = { _ -> false }
        }
        When("Choosing a topic") {
            inProgressLessonOrError = chooseTopic(isAttendanceChecked)(topicTitle, lessonId)
        }
        Then("Lesson is in progress") {
            assertThat(inProgressLessonOrError.isSuccess()).isTrue()
            assertThat(inProgressLessonOrError.getSuccess()).isEqualToComparingFieldByField(InProgressLesson(lessonId, LessonTopic(topicTitle)))
        }
        Then("Attendance is not checked error is returned") {
            assertThat(inProgressLessonOrError.isError()).isTrue()
            assertThat(inProgressLessonOrError.getError()).isInstanceOf(ChooseTopicError.AttendanceIsNotChecked::class.java)
        }
    }
}