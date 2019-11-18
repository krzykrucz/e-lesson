package com.krzykrucz.elesson.currentlesson.startlesson.domain

import arrow.core.Either
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.shared.Teacher
import java.time.LocalDateTime


typealias FetchScheduledLesson = (Teacher, LocalDateTime) -> IO<Either<StartLessonError, ScheduledLesson>>
typealias FetchClassRegistry = (ClassName) -> IO<Either<StartLessonError, ClassRegistry>>// TODO can be removed if class is an aggregate root

typealias LessonStartTime = LocalDateTime
typealias ValidateLessonStartTime = (ScheduledLesson, LocalDateTime) -> Either<StartLessonError, ValidatedScheduledLesson>
typealias StartLesson = (Teacher, LessonStartTime) -> IO<Either<StartLessonError, StartedLesson>>
