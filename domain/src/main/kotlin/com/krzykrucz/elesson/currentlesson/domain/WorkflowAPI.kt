package com.krzykrucz.elesson.currentlesson.domain

import java.time.LocalDateTime


typealias CheckScheduledLesson = (Teacher, LocalDateTime) -> AsyncOutput<ScheduledLesson>
typealias FetchClassRegistry = (ClassName) -> AsyncOutput<ClassRegistry>
typealias StartLesson = (FetchClassRegistry, CheckScheduledLesson, Teacher, LocalDateTime) -> AsyncOutput<LessonBeforeAttendance>