package com.krzykrucz.elesson.currentlesson.adapters.preparedness.persistence

import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.PersistUnpreparedStudentToLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

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

    @EventListener
    fun eventListener(event: StudentMarkedUnprepared) {
        runBlocking {
            persistUnpreparedStudentToLesson(event)
        }
    }
}
