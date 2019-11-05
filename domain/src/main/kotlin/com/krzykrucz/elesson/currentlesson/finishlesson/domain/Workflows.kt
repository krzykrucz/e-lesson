package com.krzykrucz.elesson.currentlesson.finishlesson.domain

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.topic.domain.InProgressLesson

typealias CheckIfBellRang = (LessonHourNumber, CurrentTime) -> LessonBell

typealias FinishLesson = (InProgressLesson, CurrentTime) -> Either<FinishLessonError, FinishedLesson>