package com.krzykrucz.elesson.currentlesson.topic.adapters.persistence

import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.topic.domain.CountFinishLessons
import com.krzykrucz.elesson.currentlesson.domain.topic.domain.FinishedLessonsCount
import com.krzykrucz.elesson.currentlesson.domain.topic.domain.PersistInProgressLesson
import com.krzykrucz.elesson.currentlesson.infrastructure.Database

fun fetchFinishedLessonsCount(): CountFinishLessons = {
    IO.just(FinishedLessonsCount(count = NaturalNumber.FOUR))
}

fun persistInProgressLesson(): PersistInProgressLesson = { inProgressLesson ->
    Database.IN_PROGRESS_LESSON_DATABASE.put(inProgressLesson.lessonIdentifier, inProgressLesson)
            .let { IO.lazy }
}