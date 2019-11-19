package com.krzykrucz.elesson.currentlesson.domain.startlesson

import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson


typealias PersistStartedLessonIfDoesNotExist = (StartedLesson) -> IO<LessonIdentifier>