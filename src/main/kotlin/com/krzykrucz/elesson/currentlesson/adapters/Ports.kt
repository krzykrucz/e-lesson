package com.krzykrucz.elesson.currentlesson.adapters

import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.domain.StartedLesson
import com.virtuslab.basetypes.result.arrow.AsyncResult
import java.lang.Exception

typealias PersistStartedLesson = (StartedLesson) -> AsyncResult<LessonIdentifier, Exception>