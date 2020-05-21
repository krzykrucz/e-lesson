package com.krzykrucz.elesson.currentlesson.domain.topic

import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier


typealias PersistInProgressLesson = suspend (LessonIdentifier, InProgressLesson) -> Unit
