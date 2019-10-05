package com.krzykrucz.elesson.currentlesson.attendance

import com.krzykrucz.elesson.currentlesson.AcceptanceSpec

class AttendanceAcceptanceSpec extends AcceptanceSpec {

    def "attendance acceptance spec"() {
        given: 'Date, lesson hour number and class name'
            def date = "2019-09-09"
            def lessonHourNumber = 1
            def className = "1A"
        when: 'Get empty attendance'
            def result = rest.getForObject(
                    serverUrl,
                    String.class,
                    [date: date, lessonHourNumber: lessonHourNumber, className: className]
            )
        then:
            println(result)

    }

}
