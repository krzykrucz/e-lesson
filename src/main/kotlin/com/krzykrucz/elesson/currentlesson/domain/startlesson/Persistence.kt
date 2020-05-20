package com.krzykrucz.elesson.currentlesson.domain.startlesson

import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson


typealias PersistStartedLessonIfDoesNotExist = suspend (StartedLesson) -> LessonIdentifier
