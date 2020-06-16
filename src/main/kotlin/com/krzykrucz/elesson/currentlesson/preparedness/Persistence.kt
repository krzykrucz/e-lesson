package com.krzykrucz.elesson.currentlesson.preparedness

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

typealias PersistUnpreparedStudentToLesson = suspend (StudentMarkedUnprepared) -> LessonIdentifier

typealias NotifyStudentMarkedUnprepared = suspend (StudentMarkedUnprepared) -> Unit
typealias FindCurrentLesson = suspend (LessonIdentifier) -> Either<UnpreparednessError.LessonNotStarted, CurrentLesson>
