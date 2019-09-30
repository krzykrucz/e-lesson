package com.krzykrucz.elesson.currentlesson.domain

import java.time.LocalDateTime


typealias CheckScheduledLesson = (Teacher, LocalDateTime) -> AsyncOutput<ScheduledLesson, Throwable>
typealias FetchClassRegistry = (ClassName) -> AsyncOutput<ClassRegistry, Throwable>
typealias CheckLessonStarted = (LessonIdentifier) -> Boolean

typealias StartLesson = (Teacher, LocalDateTime) -> AsyncOutput<LessonBeforeAttendance, StartLessonError>