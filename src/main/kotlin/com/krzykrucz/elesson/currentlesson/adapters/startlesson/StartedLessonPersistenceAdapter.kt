package com.krzykrucz.elesson.currentlesson.adapters.startlesson

import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgress
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.PersistStartedLessonIfDoesNotExist
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.LESSON_DATABASE
import com.krzykrucz.elesson.currentlesson.infrastructure.PersistentCurrentLesson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


// TODO maybe check if lesson not started somewhere else
private val persistStartedLessonInRepository: PersistStartedLessonIfDoesNotExist =
    { lesson ->
        if (!StartedLessonInMemoryRepository.contains(lesson.id)) {
            StartedLessonInMemoryRepository.store(lesson)
        }
        lesson.id
    }

private object StartedLessonInMemoryRepository {

    fun store(startedLesson: StartedLesson) =
        LESSON_DATABASE.put(startedLesson.id,
            PersistentCurrentLesson(
                lessonId = startedLesson.id,
                classRegistry = startedLesson.clazz,
                subject = startedLesson.subject,
                status = InProgress
            )
        )

    fun contains(lessonIdentifier: LessonIdentifier): Boolean = LESSON_DATABASE.containsKey(lessonIdentifier)

    fun get(lessonIdentifier: LessonIdentifier): Option<StartedLesson> =
        LESSON_DATABASE[lessonIdentifier]
            .let { Option.fromNullable(it) }
            .map {
                StartedLesson(
                    it.lessonId,
                    it.classRegistry,
                    it.subject
                )
            }

}


@Configuration
class StartedLessonPersistenceAdapterConfig {

    @Bean
    fun create(): PersistStartedLessonIfDoesNotExist =
        persistStartedLessonInRepository
}
