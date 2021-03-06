package com.krzykrucz.elesson.currentlesson.adapters.preparedness

import arrow.core.None
import com.krzykrucz.elesson.currentlesson.adapters.AcceptanceSpec
import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import com.krzykrucz.elesson.currentlesson.infrastructure.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.adapters.preparedness.readmodel.StudentInSemesterReadModel
import com.krzykrucz.elesson.currentlesson.adapters.preparedness.rest.ReportUnpreparedRequest
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendanceList
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
                new StudentsUnpreparedForLesson([])
        )
        Database.LESSON_DATABASE.put(lessonId, lessonWithAttendanceChecked)
        final request = new ReportUnpreparedRequest(
                new StudentReportingUnpreparedness('Draco', 'Malfoy'),
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
        final unpreparedStudents = Database.LESSON_DATABASE.get(lessonId).unpreparedStudents.students
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
        StudentInSemesterReadModel.READ_MODEL == [
                (expectedStudentInSemester): expectedUnpreparedness
        ]
    }
}
