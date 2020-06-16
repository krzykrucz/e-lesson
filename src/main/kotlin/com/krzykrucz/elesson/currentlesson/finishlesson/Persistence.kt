package com.krzykrucz.elesson.currentlesson.finishlesson

import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

typealias StoreLessonAsFinished = suspend (LessonIdentifier) -> Unit
