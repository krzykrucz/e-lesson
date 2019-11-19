package com.krzykrucz.elesson.currentlesson.adapters.preparedness.persistence

import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.PersistUnpreparedStudentToLesson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import arrow.effects.IO.Companion as IO

private val persistUnpreparedStudentToLesson: PersistUnpreparedStudentToLesson = { event ->
    Database.LESSON_DATABASE.compute(event.lessonId) { _, currentLesson ->
        currentLesson?.copy(unpreparedStudents = event.studentsUnpreparedForLesson)
    }
    event.lessonId
        .let(IO::just)
}

@Configuration
class PersistUnpreparedStudentToLessonAdapter {
    @Bean
    fun persistUnpreparedStudentToLessonBean() =
        persistUnpreparedStudentToLesson
}