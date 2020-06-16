package com.krzykrucz.elesson.currentlesson.finishlesson

import arrow.core.Either
import arrow.core.maybe
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber

typealias CheckIfBellRang = (LessonHourNumber, FinishLessonTime) -> Boolean

private val bellRang: CheckIfBellRang = { lessonHourNumber, finishLessonTime ->
    lessonHourNumber.run {
        finishLessonTime.isAfter(lessonScheduledEndTime())
    }
}

typealias FinishLesson = (InProgressLesson, FinishLessonTime) -> Either<FinishLessonError, FinishedLesson>

fun finishLessonWorkflow(checkIfBellRang: CheckIfBellRang = bellRang): FinishLesson = { inProgressLesson, currentTime ->
    inProgressLesson
        .lessonIdentifier
        .lessonHourNumber
        .let { checkIfBellRang(it, currentTime) }
        .maybe { FinishedLesson(inProgressLesson.lessonTopic) }
        .toEither { FinishLessonError.BellNotRang() }
}
