package com.krzykrucz.elesson.currentlesson

import arrow.core.None
import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.attendance.AbsentStudent
import com.krzykrucz.elesson.currentlesson.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.lessonprogress.LessonProgressDto
import com.krzykrucz.elesson.currentlesson.preparedness.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.shared.InProgress
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
                new LessonSubject(new NonEmptyText("Potions")),
                new InProgress(),
                new StudentsUnpreparedForLesson([])
        )
        Database.LESSON_DATABASE.put(lessonId, currentLesson)

        when: 'Fetching lesson progress view'
        def lessonProgress = rest.exchange(
                "/progress?date=${lessonId.date.toString()}" +
                        "&lessonHourNumber=${lessonId.lessonHourNumber.number.number}" +
                        "&className=${lessonId.className.name.text}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                LessonProgressDto
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
