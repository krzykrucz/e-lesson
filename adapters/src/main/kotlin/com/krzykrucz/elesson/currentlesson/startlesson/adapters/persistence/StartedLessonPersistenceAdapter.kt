package com.krzykrucz.elesson.currentlesson.startlesson.adapters.persistence

import arrow.core.Option
import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.startlesson.domain.PersistStartedLessonIfDoesNotExist
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartedLesson
import com.krzykrucz.elesson.currentlesson.monolith.Database.Companion.STARTED_LESSON_DATABASE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class StartedLessonInMemoryRepository {

    fun store(startedLesson: StartedLesson) =
        STARTED_LESSON_DATABASE.put(startedLesson.id, startedLesson)

    fun contains(lessonIdentifier: LessonIdentifier): Boolean = STARTED_LESSON_DATABASE.containsKey(lessonIdentifier)

    fun get(lessonIdentifier: LessonIdentifier): Option<StartedLesson> =
        STARTED_LESSON_DATABASE[lessonIdentifier]
            .let { Option.fromNullable(it) }

}

class PersistStartedLessonInRepository(
    private val repository: StartedLessonInMemoryRepository
) : PersistStartedLessonIfDoesNotExist { // TODO maybe check if lesson not started somewhere else
    override fun invoke(lesson: StartedLesson): IO<LessonIdentifier> = IO {
        if (!repository.contains(lesson.id)) {
            repository.store(lesson)
        }
        lesson.id
    }

}

@Configuration
class StartedLessonPersistenceAdapterConfig {
    val repo = StartedLessonInMemoryRepository()
    @Bean
    fun create(): PersistStartedLessonIfDoesNotExist = PersistStartedLessonInRepository(repo)
}