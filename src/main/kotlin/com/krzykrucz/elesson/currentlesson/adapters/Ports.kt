package com.krzykrucz.elesson.currentlesson.adapters

import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.domain.StartedLesson

typealias PersistStartedLesson = (StartedLesson) -> IO<LessonIdentifier>