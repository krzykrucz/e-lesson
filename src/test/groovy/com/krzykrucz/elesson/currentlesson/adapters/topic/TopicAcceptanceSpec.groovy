package com.krzykrucz.elesson.currentlesson.adapters.topic

import arrow.core.None
import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.adapters.AcceptanceSpec
import com.krzykrucz.elesson.currentlesson.domain.attendance.AbsentStudent
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgress
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.TopicTitle
import com.krzykrucz.elesson.currentlesson.domain.shared.WinterSemester
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

import java.time.LocalDate

class TopicAcceptanceSpec extends AcceptanceSpec {

    def "choose topic acceptance spec"() {
        given: 'choose topic dto and checked attendance in database'
        def lessonId = new LessonIdentifier(
                LocalDate.parse("2019-09-09"),
                new LessonHourNumber(NaturalNumber.TWO),
                className1A()
        )
        def classRegistry = classRegistry1A()
        def lessonWithAttendanceChecked = new PersistentCurrentLesson(
                lessonId, classRegistry, new None(),
                new CheckedAttendanceList(new ArrayList<PresentStudent>(), new ArrayList<AbsentStudent>()),
                new WinterSemester(),
                new LessonSubject(new NonEmptyText("Elixirs")),
                new InProgress(),
                new StudentsUnpreparedForLesson([])
        )
        Database.LESSON_DATABASE.put(lessonId, lessonWithAttendanceChecked)
        def title = new TopicTitle(new NonEmptyText("Three unforgivable curses"))
        def chooseTopicDto = new com.krzykrucz.elesson.currentlesson.adapters.topic.ChooseTopicDto(lessonId, title)

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
                new LessonTopic(new LessonOrdinalInSemester(new NaturalNumber(1)), title, lessonId.date)

    }
}
