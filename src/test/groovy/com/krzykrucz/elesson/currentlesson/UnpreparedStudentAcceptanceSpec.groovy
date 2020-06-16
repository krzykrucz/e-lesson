package com.krzykrucz.elesson.currentlesson

import arrow.core.None
import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.preparedness.ReportUnpreparedRequest
import com.krzykrucz.elesson.currentlesson.preparedness.StudentInSemester
import com.krzykrucz.elesson.currentlesson.preparedness.StudentInSemesterReadModel
import com.krzykrucz.elesson.currentlesson.preparedness.StudentReportingUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.preparedness.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.shared.InProgress
import com.krzykrucz.elesson.currentlesson.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.WinterSemester
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
