package com.krzykrucz.elesson.currentlesson

import arrow.core.None
import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.attendance.AbsentStudent
import com.krzykrucz.elesson.currentlesson.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.preparedness.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.shared.InProgress
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.TopicTitle
import com.krzykrucz.elesson.currentlesson.shared.WinterSemester
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
        def chooseTopicDto = new com.krzykrucz.elesson.currentlesson.topic.ChooseTopicDto(lessonId, title)

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
