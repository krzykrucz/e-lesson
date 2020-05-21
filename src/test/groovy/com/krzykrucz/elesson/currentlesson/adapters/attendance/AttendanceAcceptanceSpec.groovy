package com.krzykrucz.elesson.currentlesson.adapters.attendance


import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.adapters.attendance.rest.AttendanceDto
import com.krzykrucz.elesson.currentlesson.adapters.attendance.rest.AttendanceResponseDto
import com.krzykrucz.elesson.currentlesson.adapters.attendance.rest.LateAttendanceDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

class AttendanceAcceptanceSpec extends AttendanceBaseSpec {

    def "attendance acceptance spec"() {
        given:
        def lessonId = lessonIdOfFirst1ALesson()
        when: 'Note Harry Potter present'
        def attendanceWithHarryPotter = rest.exchange(
                "/attendance/present",
                HttpMethod.POST,
                new HttpEntity<>(new AttendanceDto(
                        uncheckedStudentOf("Harry", "Potter", 1),
                        lessonId
                )),
                AttendanceResponseDto
        )

        then: 'Attendance is not checked yet'
        !attendanceWithHarryPotter.body.checked

        when: 'Note Tom Riddle absent and attendance is checked, class has only 2 students'
        def attendanceWithTomRiddle = rest.exchange(
                "/attendance/absent",
                HttpMethod.POST,
                new HttpEntity<>(new AttendanceDto(
                        uncheckedStudentOf("Tom", "Riddle", 2),
                        lessonId
                )),
                AttendanceResponseDto
        ).body

        then: 'Attendance is checked'
        attendanceWithTomRiddle.checked

        when: 'Tom Riddle shows up, we note him late'
        def attendanceWithLateTomRiddle = rest.exchange(
                "/attendance/late",
                HttpMethod.POST,
                new HttpEntity<>(new LateAttendanceDto(
                        lessonIdOfFirst1ALesson(),
                        absentStudentOf("Tom", "Riddle", 2),
                        "2019-09-09T10:10:00"
                )),
                AttendanceResponseDto
        ).body

        then: 'Attendance is still checked'
        attendanceWithLateTomRiddle.checked

        and: 'It is checked with Tom and Harry present and no absent students'
        def checkedAttendanceOf1A = Database.LESSON_DATABASE.get(lessonId).attendance
        checkedAttendanceOf1A.presentStudents == [
                presentStudentOf("Harry", "Potter", 1),
                presentStudentOf("Tom", "Riddle", 2),
        ]
        checkedAttendanceOf1A.absentStudents == []


    }


}
