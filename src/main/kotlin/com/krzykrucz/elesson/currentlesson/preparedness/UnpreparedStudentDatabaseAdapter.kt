package com.krzykrucz.elesson.currentlesson.preparedness

import com.krzykrucz.elesson.currentlesson.Database
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
