package com.krzykrucz.elesson.currentlesson.adapters

import com.krzykrucz.elesson.currentlesson.domain.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

import java.time.LocalDate

class StartLessonAcceptanceSpec extends AcceptanceSpec {

    final static TODAY = LocalDate.now()

    def "should start lesson"() {
        when: 'Dark Arts classes started by Severus Snape'
        def lessonIdAndStudents = rest.exchange(
                "/startlesson",
                HttpMethod.POST,
                new HttpEntity<>(new StartLessonRequest('Severus Snape')),
                ClassRegistryResponse
        ).body

        then:
        lessonIdAndStudents.lessonId.className.name.text == 'Gryffindor'
        lessonIdAndStudents.lessonId.date == TODAY
        lessonIdAndStudents.lessonId.lessonHourNumber == new LessonHourNumber.One()
        lessonIdAndStudents.students*.name == ['Harry Potter', 'Hermione Granger']

        and:
        Database.LESSON_DATABASE.containsKey(lessonIdAndStudents.lessonId)
    }

}