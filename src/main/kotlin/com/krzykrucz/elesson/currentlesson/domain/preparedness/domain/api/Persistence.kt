package com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier

//persistence
typealias PersistUnpreparedStudentToLesson = suspend (StudentMarkedUnprepared) -> LessonIdentifier

typealias NotifyStudentMarkedUnprepared = suspend (StudentMarkedUnprepared) -> Unit
typealias FindCurrentLesson = suspend (LessonIdentifier) -> Either<UnpreparednessError.LessonNotStarted, CurrentLesson>
