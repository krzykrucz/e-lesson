package com.krzykrucz.elesson.currentlesson.startlesson.domain

import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.StartedLesson


typealias PersistStartedLessonIfDoesNotExist = (StartedLesson) -> IO<LessonIdentifier>