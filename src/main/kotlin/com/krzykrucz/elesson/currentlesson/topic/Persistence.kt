package com.krzykrucz.elesson.currentlesson.topic

import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier


typealias PersistInProgressLesson = suspend (LessonIdentifier, InProgressLesson) -> Unit
