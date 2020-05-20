package com.krzykrucz.elesson.currentlesson.domain.startlesson

import arrow.core.Either
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.Teacher
import java.time.LocalDateTime


typealias FetchScheduledLesson = suspend (Teacher, LocalDateTime) ->  Either<StartLessonError, ScheduledLesson>
typealias FetchClassRegistry = suspend (ClassName) -> Either<StartLessonError, ClassRegistry>// TODO can be removed if class is an aggregate root

typealias LessonStartTime = LocalDateTime
typealias ValidateLessonStartTime = (ScheduledLesson, LocalDateTime) -> Either<StartLessonError, ValidatedScheduledLesson>
typealias StartLesson = suspend (Teacher, LessonStartTime) -> Either<StartLessonError, StartedLesson>
