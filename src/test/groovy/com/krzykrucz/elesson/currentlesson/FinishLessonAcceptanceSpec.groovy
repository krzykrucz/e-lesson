package com.krzykrucz.elesson.currentlesson

import arrow.core.Some
import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.attendance.AbsentStudent
import com.krzykrucz.elesson.currentlesson.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.finishlesson.FinishLessonDto
import com.krzykrucz.elesson.currentlesson.preparedness.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.shared.Finished
import com.krzykrucz.elesson.currentlesson.shared.InProgress
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

class FinishLessonAcceptanceSpec extends AcceptanceSpec {

    def "should finish in progress lesson"() {
        given: "in progress lesson"
        def lessonId = lessonIdOfFirst1ALesson()
        def currentLesson
        currentLesson = new PersistentCurrentLesson(
                lessonId,
                classRegistry1A(),
                new Some<LessonTopic>(
                        new LessonTopic(
                                new LessonOrdinalInSemester(new NaturalNumber(1)),
                                new TopicTitle(new NonEmptyText("Three forbidden spells")),
                                lessonId.date)
                ),
                new CheckedAttendanceList(new ArrayList<PresentStudent>(), new ArrayList<AbsentStudent>()),
                new WinterSemester(),
                new LessonSubject(new NonEmptyText("Potions")),
                new InProgress(),
                new StudentsUnpreparedForLesson([])
        )
        Database.LESSON_DATABASE.put(lessonId, currentLesson)

        when: "finishing in progress lesson"
        def dto = new FinishLessonDto(lessonId)
        def finishedLesson = rest.exchange(
                "/finished-lessons",
                HttpMethod.PUT,
                new HttpEntity<>(dto),
                String
        )

        then: "in progress lesson is finished"
        finishedLesson.statusCode == HttpStatus.OK
        Database.LESSON_DATABASE.get(lessonId).status.status == new Finished().status
    }
}
