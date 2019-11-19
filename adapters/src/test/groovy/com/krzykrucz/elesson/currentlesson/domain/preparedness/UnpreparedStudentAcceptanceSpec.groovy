package com.krzykrucz.elesson.currentlesson.domain.preparedness

import arrow.core.None
import com.krzykrucz.elesson.currentlesson.AcceptanceSpec
import com.krzykrucz.elesson.currentlesson.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.monolith.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.adapters.readmodel.StudentInSemesterReadModel
import com.krzykrucz.elesson.currentlesson.domain.preparedness.adapters.rest.ReportUnpreparedRequest
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentReportingUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentInSemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgress
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.WinterSemester
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

import static com.krzykrucz.elesson.currentlesson.monolith.Database.LESSON_DATABASE

class UnpreparedStudentAcceptanceSpec extends AcceptanceSpec {

    def "should report student unprepared"() {
        given:
        final lessonId = lessonIdOfFirst1ALesson()
        final classRegistry = classRegistry1A()
        final lessonWithAttendanceChecked = new PersistentCurrentLesson(
                lessonId,
                classRegistry,
                new None(),
                new CheckedAttendanceList([presentStudent('Draco', 'Malfoy', 1)], []),
                new WinterSemester(),
                new LessonSubject(new NonEmptyText('Dark Arts')),
                new InProgress(),
                new com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentsUnpreparedForLesson([])
        )
        LESSON_DATABASE.put(lessonId, lessonWithAttendanceChecked)
        final request = new com.krzykrucz.elesson.currentlesson.domain.preparedness.adapters.rest.ReportUnpreparedRequest(
                new com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentReportingUnpreparedness('Draco', 'Malfoy'),
                lessonId
        )

        when:
        def reponse = rest.exchange(
                "/unprepared",
                HttpMethod.POST,
                new HttpEntity<>(request),
                String.class
        )

        then: 'response is 200'
        reponse.statusCode == HttpStatus.OK

        and: 'student is unprepared'
        final unpreparedStudents = LESSON_DATABASE.get(lessonId).unpreparedStudents.students
        unpreparedStudents*.firstName*.name*.text == ['Draco']
        unpreparedStudents*.secondName*.name*.text == ['Malfoy']

        and: 'read model updated'
        final expectedStudentInSemester = new StudentInSemester(
                className1A(),
                firstName('Draco'),
                secondName('Malfoy')
        )
        final expectedUnpreparedness = new StudentSubjectUnpreparednessInASemester(
                expectedStudentInSemester,
                wholeNumber(1)
        )
        com.krzykrucz.elesson.currentlesson.domain.preparedness.adapters.readmodel.StudentInSemesterReadModel.READ_MODEL == [
                (expectedStudentInSemester): expectedUnpreparedness
        ]
    }
}
