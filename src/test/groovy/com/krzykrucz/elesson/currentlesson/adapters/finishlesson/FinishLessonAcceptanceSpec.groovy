package com.krzykrucz.elesson.currentlesson.adapters.finishlesson

import arrow.core.Some
import com.krzykrucz.elesson.currentlesson.adapters.AcceptanceSpec
import com.krzykrucz.elesson.currentlesson.domain.attendance.AbsentStudent
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.Finished
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgress
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.TopicTitle
import com.krzykrucz.elesson.currentlesson.domain.shared.WinterSemester
import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import com.krzykrucz.elesson.currentlesson.infrastructure.PersistentCurrentLesson
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
