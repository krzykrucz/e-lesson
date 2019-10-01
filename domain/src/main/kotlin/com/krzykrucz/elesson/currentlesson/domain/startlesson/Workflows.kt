package com.krzykrucz.elesson.currentlesson.domain.startlesson

import com.krzykrucz.elesson.currentlesson.domain.AsyncOutput
import java.time.LocalDateTime


typealias CheckScheduledLesson = (Teacher, LocalDateTime) -> AsyncOutput<ScheduledLesson, Throwable>
typealias FetchClassRegistry = (ClassName) -> AsyncOutput<ClassRegistry, Throwable>// TODO can be removed if class is an aggregate root
typealias CheckLessonStarted = (LessonIdentifier) -> Boolean // TODO can be removed if subject/class is an aggregate root

typealias AttemptedLessonStartTime = LocalDateTime
typealias StartLesson = (Teacher, AttemptedLessonStartTime) -> AsyncOutput<StartedLesson, StartLessonError>