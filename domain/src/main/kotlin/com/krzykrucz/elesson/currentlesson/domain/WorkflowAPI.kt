package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.Option
import arrow.effects.IO
import java.time.LocalDateTime


typealias CheckScheduledLesson = (Teacher, LocalDateTime) -> IO<ScheduledLesson>
typealias FetchClassRegistry = (ClassName) -> IO<ClassRegistry>
typealias StartLesson = (FetchClassRegistry, CheckScheduledLesson, Teacher, LocalDateTime) -> Option<LessonIntroduction>