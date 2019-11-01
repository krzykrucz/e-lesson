package com.krzykrucz.elesson.currentlesson.topic.usecase.persistence

import arrow.core.getOrElse
import arrow.core.some
import arrow.core.toOption
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.lessonprogress.usecase.Finished
import com.krzykrucz.elesson.currentlesson.monolith.Database
import com.krzykrucz.elesson.currentlesson.topic.domain.CheckIfAttendanceIsChecked
import com.krzykrucz.elesson.currentlesson.topic.domain.CountFinishedLessons
import com.krzykrucz.elesson.currentlesson.topic.domain.FinishedLessonsCount
import com.krzykrucz.elesson.currentlesson.topic.domain.PersistInProgressLesson


fun fetchFinishedLessonsCount(): CountFinishedLessons = {
    Database.LESSON_DATABASE.values.stream()
        .filter { it.status == Finished }
        .count()
        .let { FinishedLessonsCount(it.toInt()) }
        .let { IO.just(it) }
}

fun checkIfAttendanceIsChecked(): CheckIfAttendanceIsChecked = { lessonId ->
    Database.LESSON_DATABASE[lessonId].toOption()
        .map { lesson -> lesson.attendance is CheckedAttendanceList }
        .getOrElse { false }
        .let { IO.just(it) }
}


fun persistInProgressLesson(): PersistInProgressLesson = { lessonId, inProgressLesson ->
    Database.LESSON_DATABASE.compute(lessonId) { _, lesson ->
        lesson?.copy(
            lessonTopic = inProgressLesson.lessonTopic.some())
    }.let { IO.lazy }
}
