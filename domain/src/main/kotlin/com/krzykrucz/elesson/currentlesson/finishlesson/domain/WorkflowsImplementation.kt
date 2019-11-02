package com.krzykrucz.elesson.currentlesson.finishlesson.domain

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.LessonBell.NOT_RANG
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.LessonBell.RANG


fun bellRang(): CheckIfBellRang = { lessonHourNumber, currentHour ->
    lessonHourNumber.run {
        if (currentHour.isAfter(lessonScheduledEndTime())) {
            RANG
        } else {
            NOT_RANG
        }
    }
}

fun finishLesson(checkIfBellRang: CheckIfBellRang): FinishLesson = { inProgressLesson, currentHour ->
    checkIfBellRang(inProgressLesson.lessonHourNumber(), currentHour).let {
        if (it == RANG) {
            Either.right(FinishedLesson(inProgressLesson.lessonTopic))
        } else {
            Either.left(FinishLessonError.BellNotRang())
        }
    }
}