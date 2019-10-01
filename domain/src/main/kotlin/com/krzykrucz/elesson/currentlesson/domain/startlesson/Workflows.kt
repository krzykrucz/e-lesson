package com.krzykrucz.elesson.currentlesson.domain.startlesson

import com.krzykrucz.elesson.currentlesson.domain.AsyncOutput
import java.time.LocalDateTime


typealias CheckScheduledLesson = (Teacher, LocalDateTime) -> AsyncOutput<ScheduledLesson, Throwable>
typealias FetchClassRegistry = (ClassName) -> AsyncOutput<ClassRegistry, Throwable>
typealias CheckLessonStarted = (LessonIdentifier) -> Boolean

typealias AttemptedLessonStartTime = LocalDateTime
typealias StartLesson = (Teacher, AttemptedLessonStartTime) -> AsyncOutput<LessonBeforeAttendance, StartLessonError>