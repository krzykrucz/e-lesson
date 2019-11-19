package com.krzykrucz.elesson.currentlesson.adapters.lessonprogress

import arrow.core.None
import com.krzykrucz.elesson.currentlesson.adapters.AcceptanceSpec
import com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.rest.LessonProgressDto
import com.krzykrucz.elesson.currentlesson.adapters.monolith.Database
import com.krzykrucz.elesson.currentlesson.adapters.monolith.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.attendance.AbsentStudent
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgress
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.WinterSemester
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
                new LessonSubject(new NonEmptyText("Potions")),
                new InProgress(),
                new StudentsUnpreparedForLesson([])
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
