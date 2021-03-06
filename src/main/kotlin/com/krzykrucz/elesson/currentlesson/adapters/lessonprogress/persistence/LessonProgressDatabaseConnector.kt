package com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.persistence

import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.usecase.LessonProgress
import com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.usecase.LessonProgressError
import com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.usecase.LoadLessonProgress
import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LessonProgressDatabaseConnector {

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
