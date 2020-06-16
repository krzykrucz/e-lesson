package com.krzykrucz.elesson.currentlesson.topic

import arrow.core.getOrElse
import arrow.core.some
import arrow.core.toOption
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.shared.Finished


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
