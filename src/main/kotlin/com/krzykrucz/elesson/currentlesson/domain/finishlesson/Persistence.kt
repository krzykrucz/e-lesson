package com.krzykrucz.elesson.currentlesson.domain.finishlesson

import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier

typealias StoreLessonAsFinished = suspend (LessonIdentifier) -> Unit
