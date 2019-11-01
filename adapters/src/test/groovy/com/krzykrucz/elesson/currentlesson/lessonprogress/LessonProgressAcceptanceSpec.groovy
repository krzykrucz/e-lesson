package com.krzykrucz.elesson.currentlesson.lessonprogress

import arrow.core.None
import com.krzykrucz.elesson.currentlesson.AcceptanceSpec
import com.krzykrucz.elesson.currentlesson.attendance.domain.AbsentStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.PresentStudent
import com.krzykrucz.elesson.currentlesson.lessonprogress.adapters.rest.LessonProgressDto
import com.krzykrucz.elesson.currentlesson.lessonprogress.usecase.InProgress
import com.krzykrucz.elesson.currentlesson.monolith.Database
import com.krzykrucz.elesson.currentlesson.monolith.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.WinterSemester
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

class LessonProgressAcceptanceSpec extends AcceptanceSpec {
    def "should fetch lesson progress view"() {
        given: 'Current lesson in database'
        def lessonId = lessonIdOfFirst1ALesson()
        def currentLesson = new PersistentCurrentLesson(
                lessonId,
                classRegistry1A(),
                new None(),
                new CheckedAttendanceList(new ArrayList<PresentStudent>(), new ArrayList<AbsentStudent>()),
                new WinterSemester(),
                new LessonSubject(new NonEmptyText("Elixirs")),
                new InProgress()
        )
        Database.LESSON_DATABASE.put(lessonId, currentLesson)

        when: 'Fetching lesson progress view'
        def lessonProgress = rest.exchange(
                "/progress?date=${lessonId.date.toString()}" +
                        "&lessonHourNumber=${lessonId.lessonHourNumber.number.number.toString()}" +
                        "&className=${lessonId.className.name.text}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                LessonProgressDto.class
        ).body

        then: 'It should create LessonProgress view of Lesson in database'
        lessonProgress == new LessonProgressDto(
                currentLesson.semester.semesterOrdinalNumber.number,
                lessonId.className.name.text,
                currentLesson.subject.subject.text,
                lessonId.date.toString(),
                currentLesson.lessonTopic.orNull(),
                currentLesson.status.status
        )
    }
}
