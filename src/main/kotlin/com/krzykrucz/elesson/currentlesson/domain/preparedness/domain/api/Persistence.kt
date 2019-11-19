package com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api

import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.domain.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.domain.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier

//persistence
typealias PersistUnpreparedStudentToLesson = (StudentMarkedUnprepared) -> IO<LessonIdentifier>

typealias NotifyStudentMarkedUnprepared = (StudentMarkedUnprepared) -> IO<Unit>
typealias FindCurrentLesson = (LessonIdentifier) -> AsyncOutput<UnpreparednessError.LessonNotStarted, CurrentLesson>