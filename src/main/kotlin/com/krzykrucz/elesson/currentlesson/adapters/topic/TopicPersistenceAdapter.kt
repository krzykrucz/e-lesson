package com.krzykrucz.elesson.currentlesson.adapters.topic

import arrow.core.getOrElse
import arrow.core.some
import arrow.core.toOption
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.shared.Finished
import com.krzykrucz.elesson.currentlesson.domain.topic.CheckIfAttendanceIsChecked
import com.krzykrucz.elesson.currentlesson.domain.topic.CountFinishedLessons
import com.krzykrucz.elesson.currentlesson.domain.topic.FinishedLessonsCount
import com.krzykrucz.elesson.currentlesson.domain.topic.PersistInProgressLesson


internal val fetchFinishedLessonsCount: CountFinishedLessons = {
    Database.LESSON_DATABASE.values.stream()
        .filter { it.status == Finished }
        .count()
        .let { FinishedLessonsCount(it.toInt()) }
}

internal val checkIfAttendanceIsChecked: CheckIfAttendanceIsChecked = { lessonId ->
    Database.LESSON_DATABASE[lessonId].toOption()
        .map { lesson -> lesson.attendance is CheckedAttendanceList }
        .getOrElse { false }
}


internal val persistInProgressLesson: PersistInProgressLesson = { lessonId, inProgressLesson ->
    Database.LESSON_DATABASE.compute(lessonId) { _, lesson ->
        lesson?.copy(
            lessonTopic = inProgressLesson.lessonTopic.some())
    }.let { IO.lazy }
}