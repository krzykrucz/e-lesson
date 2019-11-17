package com.krzykrucz.elesson.currentlesson.finishlesson.domain

import com.krzykrucz.elesson.currentlesson.finishlesson.domain.LessonBell.NOT_RANG
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.LessonBell.RANG


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
        .lessonHourNumber()
        .map { checkIfBellRang(it, currentTime) }
        .filter { it == RANG }
        .map { FinishedLesson(inProgressLesson.lessonTopic) }
        .toEither { FinishLessonError.BellNotRang() }
}