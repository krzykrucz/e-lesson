package com.krzykrucz.elesson.currentlesson.preparedness

import com.krzykrucz.elesson.currentlesson.AcceptanceSpec
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.monolith.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.preparedness.adapters.readmodel.StudentInSemesterReadModel
import com.krzykrucz.elesson.currentlesson.preparedness.adapters.rest.ReportUnpreparedRequest
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.StudentReportingUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentInSemester
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
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
                null,
                new CheckedAttendanceList([presentStudent('Draco', 'Malfoy', 1)], []),
                new StudentsUnpreparedForLesson([])
        )
        LESSON_DATABASE.put(lessonId, lessonWithAttendanceChecked)
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
        StudentInSemesterReadModel.READ_MODEL == [
                (expectedStudentInSemester): expectedUnpreparedness
        ]
    }
}
