package com.krzykrucz.elesson.currentlesson.finishlesson.domain

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.topic.domain.InProgressLesson

typealias CheckIfBellRang = (LessonHourNumber, CurrentHour) -> LessonBell

typealias FinishLesson = (InProgressLesson, CurrentHour) -> Either<FinishLessonError, FinishedLesson>