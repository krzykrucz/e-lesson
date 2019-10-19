package com.krzykrucz.elesson.currentlesson.topic

import com.krzykrucz.elesson.currentlesson.AcceptanceSpec
import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.attendance.AbsentStudent
import com.krzykrucz.elesson.currentlesson.domain.attendance.AttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.domain.topic.domain.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.topic.domain.LessonTopic
import com.krzykrucz.elesson.currentlesson.domain.topic.domain.TopicTitle
import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import com.krzykrucz.elesson.currentlesson.topic.adapters.rest.ChooseTopicDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

class TopicAcceptanceSpec extends AcceptanceSpec {

    def "choose topic acceptance spec"() {
        given: 'choose topic dto and checked attendance in database'
            def lessonId = lessonIdOfFirst1ALesson()
            Database.ATTENDANCE_DATABASE.put(lessonId, new CheckedAttendance(new AttendanceList(
                    lessonId.className,
                    lessonId.date,
                    lessonId.lessonHourNumber,
                    new ArrayList<PresentStudent>(),
                    new ArrayList<AbsentStudent>(),
            )))
            def title = new TopicTitle(new NonEmptyText("Three forbidden spells"))
            def chooseTopicDto = new ChooseTopicDto(lessonId, title)

        when: 'choosing topic for current lesson'
            def inProgressLesson = rest.exchange(
                    serverUrl + "/topic",
                    HttpMethod.POST,
                    new HttpEntity<>(chooseTopicDto),
                    InProgressLesson.class
            )

        then: 'In progress lesson is persisted to database'
            inProgressLesson.body == new InProgressLesson(
                    lessonId,
                    new LessonTopic(title),
                    NaturalNumber.FIVE
            )
            Database.IN_PROGRESS_LESSON_DATABASE.containsKey(lessonId)
    }
}
