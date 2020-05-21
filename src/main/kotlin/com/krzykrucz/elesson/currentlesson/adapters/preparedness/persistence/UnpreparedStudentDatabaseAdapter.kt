package com.krzykrucz.elesson.currentlesson.adapters.preparedness.persistence

import com.krzykrucz.elesson.currentlesson.domain.preparedness.PersistUnpreparedStudentToLesson
import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val persistUnpreparedStudentToLesson: PersistUnpreparedStudentToLesson = { event ->
    Database.LESSON_DATABASE.compute(event.lessonId) { _, currentLesson ->
        currentLesson?.copy(unpreparedStudents = event.studentsUnpreparedForLesson)
    }
    event.lessonId
}

@Configuration
class PersistUnpreparedStudentToLessonAdapter {
    @Bean
    fun persistUnpreparedStudentToLessonBean() =
        persistUnpreparedStudentToLesson
}
