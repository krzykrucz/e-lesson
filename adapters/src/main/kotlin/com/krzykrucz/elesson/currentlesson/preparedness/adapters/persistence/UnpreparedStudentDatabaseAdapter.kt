package com.krzykrucz.elesson.currentlesson.preparedness.adapters.persistence

import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.monolith.Database
import com.krzykrucz.elesson.currentlesson.preparedness.domain.PersistUnpreparedStudentToLesson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

val persistUnpreparedStudentToLesson: PersistUnpreparedStudentToLesson = { event ->
    Database.LESSON_DATABASE.compute(event.lessonId) { id, currentLesson ->
        currentLesson?.copy(unpreparedStudents = event.studentsUnpreparedForLesson)
    }
    event.lessonId
        .let { IO.just(it) }
}

@Configuration
class PersistUnpreparedStudentToLessonAdapter {
    @Bean
    fun persistUnpreparedStudentToLessonBean() = persistUnpreparedStudentToLesson
}