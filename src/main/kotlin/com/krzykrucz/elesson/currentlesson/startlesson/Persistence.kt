package com.krzykrucz.elesson.currentlesson.startlesson

import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.StartedLesson


typealias PersistStartedLesson = suspend (StartedLesson) -> LessonIdentifier
