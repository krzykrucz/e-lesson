package com.krzykrucz.elesson.currentlesson.startlesson

import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.Database.LESSON_DATABASE
import com.krzykrucz.elesson.currentlesson.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.shared.InProgress
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.StartedLesson


// TODO maybe check if lesson not started somewhere else
internal val startedLessonPersistenceAdapter: PersistStartedLesson =
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
