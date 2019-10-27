package com.krzykrucz.elesson.currentlesson.topic

import arrow.core.None
import com.krzykrucz.elesson.currentlesson.AcceptanceSpec
import com.krzykrucz.elesson.currentlesson.attendance.domain.AbsentStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.PresentStudent
import com.krzykrucz.elesson.currentlesson.lessonprogress.InProgress
import com.krzykrucz.elesson.currentlesson.monolith.Database
import com.krzykrucz.elesson.currentlesson.monolith.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.shared.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class TopicAcceptanceSpec extends AcceptanceSpec {

    def "choose topic acceptance spec"() {
        given: 'choose topic dto and checked attendance in database'
        def lessonId = lessonIdOfFirst1ALesson()
        def classRegistry = classRegistry1A()
        def lessonWithAttendanceChecked = new PersistentCurrentLesson(
                lessonId, classRegistry, new None(),
                new CheckedAttendanceList(new ArrayList<PresentStudent>(), new ArrayList<AbsentStudent>()),
                new WinterSemester(),
                new LessonSubject(new NonEmptyText("Elixirs")),
                new InProgress()
        )
        Database.LESSON_DATABASE.put(lessonId, lessonWithAttendanceChecked)
        def title = new TopicTitle(new NonEmptyText("Three forbidden spells"))
        def chooseTopicDto = new ChooseTopicDto(lessonId, title, "2019-09-09")

        when: 'choosing topic for current lesson'
        def inProgressLesson = rest.exchange(
                "/topic",
                HttpMethod.POST,
                new HttpEntity<>(chooseTopicDto),
                String.class
        )

        then: 'In progress lesson is persisted to database'
        inProgressLesson.statusCode == HttpStatus.OK
        Database.LESSON_DATABASE.get(lessonId).lessonTopic.orNull() ==
                new LessonTopic(new LessonOrdinalNumber(new NaturalNumber(1)), title, lessonId.date)

    }
}
