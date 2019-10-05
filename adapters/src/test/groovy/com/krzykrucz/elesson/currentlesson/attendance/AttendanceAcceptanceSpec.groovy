package com.krzykrucz.elesson.currentlesson.attendance


import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.NotCompletedAttendance
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

class AttendanceAcceptanceSpec extends AttendanceBaseSpec {

    def "attendance acceptance spec"() {
        given: 'Date, lesson hour number and class name'
            def date = "2019-09-09"
            def lessonHourNumber = 1
            def className = "1A"

        when: 'Get attendance of first lesson of 1A'
            def emptyAttendanceOf1A = rest.getForObject(
                    serverUrl + "/attendance?date=${date}&lessonHourNumber=${lessonHourNumber}&className=${className}",
                    NotCompletedAttendance.class
            )

        then: 'Result is an empty attendance'
            emptyAttendanceOf1A == createEmptyAttendance()

        when: 'Note Harry Potter present'
            def attendanceWithHarryPotter = rest.exchange(
                    serverUrl + "/attendance/present",
                    HttpMethod.POST,
                    new HttpEntity<>(new AttendanceDto(
                            uncheckedStudentOf("Harry", "Potter", 1),
                            emptyAttendanceOf1A,
                            className1A())
                    ),
                    NotCompletedAttendance.class
            ).body

        then: 'Present students list is updated with Harry Potter'
            attendanceWithHarryPotter.attendance.presentStudents == [presentStudentOf("Harry", "Potter", 1)]

        when: 'Note Tom Riddle absent and attendance is checked, class has only 2 students'
            def attendanceWithTomRiddle = rest.exchange(
                    serverUrl + "/attendance/absent",
                    HttpMethod.POST,
                    new HttpEntity<>(new AttendanceDto(
                            uncheckedStudentOf("Tom", "Riddle", 2),
                            attendanceWithHarryPotter,
                            className1A())
                    ),
                    CheckedAttendance.class
            ).body

        then: 'Absent student list is updated with Tom Riddle'
            attendanceWithTomRiddle.attendance.absentStudents == [absentStudentOf("Tom", "Riddle", 2)]
            attendanceWithHarryPotter.attendance.presentStudents == [presentStudentOf("Harry", "Potter", 1)]

        when: 'Tom Riddle shows up, we note him late'
            def attendanceWithLateTomRiddle = rest.exchange(
                    serverUrl + "/attendance/late",
                    HttpMethod.POST,
                    new HttpEntity<>(new LateAttendanceDto(
                            lessonIdOfFirst1ALesson(),
                            absentStudentOf("Tom", "Riddle", 2),
                            attendanceWithTomRiddle,
                            "2019-09-09T10:10:00"
                    )),
                    CheckedAttendance.class
            ).body

        then: 'No one is absent, Harry and Tom are present'
            attendanceWithLateTomRiddle.attendance.absentStudents == []
            attendanceWithLateTomRiddle.attendance.presentStudents == [
                    presentStudentOf("Harry", "Potter", 1),
                    presentStudentOf("Tom", "Riddle", 2),
            ]

        when: 'Mark attendance as finished'
           def finishAttendanceResponse = rest.exchange(
                    serverUrl + "/attendance",
                    HttpMethod.POST,
                    new HttpEntity<>(new FinishAttendanceDto(lessonIdOfFirst1ALesson(), attendanceWithLateTomRiddle)),
                    CheckedAttendance.class
            ).statusCodeValue

        then: 'Finishing attendance is successful'
            finishAttendanceResponse == 200

        when: 'Get attendance of first lesson of 1A'
            def checkedAttendanceOf1A = rest.getForObject(
                    serverUrl + "/attendance?date=${date}&lessonHourNumber=${lessonHourNumber}&className=${className}",
                    CheckedAttendance.class
            )

        then: 'It is checked with Tom and Harry present and no absent students'
            checkedAttendanceOf1A.attendance.presentStudents == [
                    presentStudentOf("Harry", "Potter", 1),
                    presentStudentOf("Tom", "Riddle", 2),
            ]
            checkedAttendanceOf1A.attendance.absentStudents == []


    }


}
