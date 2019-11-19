package com.krzykrucz.elesson.currentlesson.domain.finishlesson

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonHourNumber

typealias CheckIfBellRang = (LessonHourNumber, FinishLessonTime) -> LessonBell

typealias FinishLesson = (InProgressLesson, FinishLessonTime) -> Either<FinishLessonError, FinishedLesson>