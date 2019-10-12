package com.krzykrucz.elesson.currentlesson.domain.startlesson

import arrow.effects.IO


typealias PersistStartedLessonIfDoesNotExist = (StartedLesson) -> IO<LessonIdentifier>