package com.krzykrucz.elesson.currentlesson.finishlesson.domain

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber

typealias CheckIfBellRang = (LessonHourNumber, FinishLessonTime) -> LessonBell

typealias FinishLesson = (InProgressLesson, FinishLessonTime) -> Either<FinishLessonError, FinishedLesson>