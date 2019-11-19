package com.krzykrucz.elesson.currentlesson.domain.finishlesson

import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.LessonBell.NOT_RANG
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.LessonBell.RANG


fun bellRang(): CheckIfBellRang = { lessonHourNumber, finishLessonTime ->
    lessonHourNumber.run {
        if (finishLessonTime.isAfter(lessonScheduledEndTime())) {
            RANG
        } else {
            NOT_RANG
        }
    }
}

fun finishLesson(checkIfBellRang: CheckIfBellRang): FinishLesson = { inProgressLesson, currentTime ->
    inProgressLesson
        .lessonIdentifier
        .lessonHourNumber
        .let { checkIfBellRang(it, currentTime) }
        .toOption()
        .filter { it == RANG }
        .map { FinishedLesson(inProgressLesson.lessonTopic) }
        .toEither { FinishLessonError.BellNotRang() }
}