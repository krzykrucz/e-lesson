package com.krzykrucz.elesson.currentlesson.adapters.lessonprogress

import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.Database
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LessonProgressDatabaseAdapter {

    @Bean
    fun loadLessonProgress(): LoadLessonProgress = { lessonIdentifier ->
        Database.LESSON_DATABASE[lessonIdentifier].toOption()
            .map { currentLesson ->
                LessonProgress(
                    semester = currentLesson.semester,
                    className = currentLesson.classRegistry.className,
                    date = currentLesson.lessonId.date,
                    subject = currentLesson.subject,
                    topic = currentLesson.lessonTopic,
                    status = currentLesson.status
                )
            }
            .toEither(ifEmpty = { LessonProgressError.LessonNotFound() })
    }

}
