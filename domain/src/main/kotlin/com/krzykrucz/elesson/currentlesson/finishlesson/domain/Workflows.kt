package com.krzykrucz.elesson.currentlesson.finishlesson.domain

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber

typealias CheckIfBellRang = (LessonHourNumber, CurrentTime) -> LessonBell

typealias FinishLesson = (InProgressLesson, CurrentTime) -> Either<FinishLessonError, FinishedLesson>