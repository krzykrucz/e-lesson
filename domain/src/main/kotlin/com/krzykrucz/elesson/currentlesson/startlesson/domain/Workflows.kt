package com.krzykrucz.elesson.currentlesson.startlesson.domain

import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.shared.Teacher
import java.time.LocalDateTime


typealias CheckScheduledLesson = (Teacher, LocalDateTime) -> AsyncOutput<ScheduledLesson, Throwable>
typealias FetchClassRegistry = (ClassName) -> AsyncOutput<ClassRegistry, Throwable>// TODO can be removed if class is an aggregate root

typealias AttemptedLessonStartTime = LocalDateTime
typealias StartLesson = (Teacher, AttemptedLessonStartTime) -> AsyncOutput<StartedLesson, StartLessonError>