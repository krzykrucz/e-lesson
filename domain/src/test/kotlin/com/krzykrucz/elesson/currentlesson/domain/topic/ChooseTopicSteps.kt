package com.krzykrucz.elesson.currentlesson.domain.topic

import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.attendance.AttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.lessonHourNumberOf
import com.krzykrucz.elesson.currentlesson.domain.newClassName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate

class ChooseTopicSteps : En {
    lateinit var topicTitle: TopicTitle
    lateinit var inProgressLesson: InProgressLesson
    lateinit var checkedAttendance: CheckedAttendance
    private val now = LocalDate.now()
    private val lessonId = LessonIdentifier(now, lessonHourNumberOf(1), newClassName("Slytherin"))

    init {
        Given("Topic title") {
            topicTitle = TopicTitle(NonEmptyText("Forbidden spells"))
        }
        Given("Checked Attendance") {
            checkedAttendance = CheckedAttendance(attendance = AttendanceList(
                    newClassName("Slytherin"),
                    now,
                    lessonHourNumberOf(1)
            ))
        }
        When("Choosing a topic") {
            inProgressLesson = chooseTopic()(topicTitle, checkedAttendance)
        }
        Then("Lesson is in progress") {
            assertThat(inProgressLesson).isEqualToComparingFieldByField(InProgressLesson(lessonId, LessonTopic(topicTitle)))
        }
    }
}