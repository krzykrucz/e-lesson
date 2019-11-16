package com.krzykrucz.elesson.currentlesson.preparedness.domain.api

import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

//persistence
typealias PersistUnpreparedStudentToLesson = (StudentMarkedUnprepared) -> IO<LessonIdentifier>

typealias NotifyStudentMarkedUnprepared = (StudentMarkedUnprepared) -> IO<Unit>
typealias FindCurrentLesson = (LessonIdentifier) -> AsyncOutput<UnpreparednessError.LessonNotStarted, CurrentLesson>