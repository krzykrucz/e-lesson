package com.krzykrucz.elesson.currentlesson.attendance

import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

class AttendanceAcceptanceSpec extends AttendanceBaseSpec {

    def "attendance acceptance spec"() {
        given:
            def lessonId = lessonIdOfFirst1ALesson()
        when: 'Note Harry Potter present'
            def attendanceWithHarryPotter = rest.exchange(
                    serverUrl + "/attendance/present",
                    HttpMethod.POST,
                    new HttpEntity<>(new AttendanceDto(
                            uncheckedStudentOf("Harry", "Potter", 1),
                            lessonId
                    )),
                    AttendanceResponseDto.class
            ).body

        then: 'Attendance is not checked yet'
        !attendanceWithHarryPotter.checked

        when: 'Note Tom Riddle absent and attendance is checked, class has only 2 students'
            def attendanceWithTomRiddle = rest.exchange(
                    serverUrl + "/attendance/absent",
                    HttpMethod.POST,
                    new HttpEntity<>(new AttendanceDto(
                            uncheckedStudentOf("Tom", "Riddle", 2),
                            lessonId
                    )),
                    AttendanceResponseDto.class
            ).body

        then: 'Attendance is checked'
            attendanceWithTomRiddle.checked

        when: 'Tom Riddle shows up, we note him late'
            def attendanceWithLateTomRiddle = rest.exchange(
                    serverUrl + "/attendance/late",
                    HttpMethod.POST,
                    new HttpEntity<>(new LateAttendanceDto(
                            lessonIdOfFirst1ALesson(),
                            absentStudentOf("Tom", "Riddle", 2),
                            "2019-09-09T10:10:00"
                    )),
                    AttendanceResponseDto.class
            ).body

        then: 'Attendance is still checked'
            attendanceWithLateTomRiddle.checked

        when: 'Get attendance of first lesson of 1A'
            def date = "2019-09-09"
            def lessonHourNumber = 1
            def className = "1A"
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
