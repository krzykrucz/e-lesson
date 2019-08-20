package com.krzykrucz.elesson.currentlesson.domain

import com.krzykrucz.elesson.currentlesson.domain.StartLessonError.*


private fun ScheduledLesson.lessonIdentifier() =
        LessonIdentifier(this.scheduledTime.toLocalDate(), this.lessonHourNumber, this.className)

private fun ScheduledLesson.toCurrentLessonWithClass(classRegistry: ClassRegistry) =
        LessonBeforeAttendance(this.lessonIdentifier(), classRegistry)


val startLesson: StartLesson = { checkLessonStarted, checkScheduledLesson, fetchClassRegistry, teacher, localDateTime ->
    checkScheduledLesson(teacher, localDateTime)
            .mapError { _ -> NotScheduledLesson() }
            .failIf({ scheduledLesson -> localDateTime.isBefore(scheduledLesson.scheduledTime) }, NotScheduledLesson())
            .failIf({ scheduledLesson -> localDateTime.isAfter(scheduledLesson.scheduledTime.plusMinutes(44)) }, NotScheduledLesson())
            .flatMapSuccess { scheduledLesson ->
                fetchClassRegistry(scheduledLesson.className)
                        .mapSuccess(scheduledLesson::toCurrentLessonWithClass)
                        .mapError { _ -> ClassRegistryUnavailable() }
            }
            .failIf({ lesson -> checkLessonStarted(lesson.id) }, LessonAlreadyStarted())
}

