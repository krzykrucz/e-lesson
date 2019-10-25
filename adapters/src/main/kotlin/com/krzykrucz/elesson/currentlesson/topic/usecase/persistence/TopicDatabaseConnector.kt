package com.krzykrucz.elesson.currentlesson.topic.usecase.persistence

import arrow.core.getOrElse
import arrow.core.toOption
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.monolith.Database
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.topic.domain.CheckIfAttendanceIsChecked
import com.krzykrucz.elesson.currentlesson.topic.domain.CountFinishedLessons
import com.krzykrucz.elesson.currentlesson.topic.domain.FinishedLessonsCount
import com.krzykrucz.elesson.currentlesson.topic.domain.PersistInProgressLesson


fun fetchFinishedLessonsCount(): CountFinishedLessons = {
    IO.just(FinishedLessonsCount(count = NaturalNumber.FOUR))
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
            lessonTopic = inProgressLesson.lessonTopic)
    }.let { IO.lazy }
}
