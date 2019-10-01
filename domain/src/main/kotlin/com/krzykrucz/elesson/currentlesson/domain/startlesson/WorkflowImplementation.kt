package com.krzykrucz.elesson.currentlesson.domain.startlesson

import com.krzykrucz.elesson.currentlesson.domain.failIf
import com.krzykrucz.elesson.currentlesson.domain.flatMapSuccess
import com.krzykrucz.elesson.currentlesson.domain.mapError
import com.krzykrucz.elesson.currentlesson.domain.mapSuccess
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartLessonError.*


private fun ScheduledLesson.lessonIdentifier() =
        LessonIdentifier(this.scheduledTime.toLocalDate(), this.lessonHourNumber, this.className)

private fun ScheduledLesson.toCurrentLessonWithClass(classRegistry: ClassRegistry, attemptedLessonStartTime: AttemptedLessonStartTime) =
        LessonBeforeAttendance(this.lessonIdentifier(), LessonStartTime(attemptedLessonStartTime), classRegistry)


fun startLesson(checkLessonStarted: CheckLessonStarted,
                checkScheduledLesson: CheckScheduledLesson,
                fetchClassRegistry: FetchClassRegistry): StartLesson = { teacher, attemptedStartTime ->
    checkScheduledLesson(teacher, attemptedStartTime)
        .mapError { _ -> NotScheduledLesson() }
        .failIf({ scheduledLesson -> attemptedStartTime.isBefore(scheduledLesson.scheduledTime) }, NotScheduledLesson())
        .failIf({ scheduledLesson -> attemptedStartTime.isAfter(scheduledLesson.scheduledTime.plusMinutes(44)) }, NotScheduledLesson())
        // ^ TODO maybe enclose these 2 mapping in the above and introduce another internal error type (lesson started too early or so)
        // ^ TODO extract a separate function with invariant
        .flatMapSuccess { scheduledLesson ->
            fetchClassRegistry(scheduledLesson.className)
                .mapSuccess { classRegistry -> scheduledLesson.toCurrentLessonWithClass(classRegistry, attemptedStartTime) }
                .mapError { _ -> ClassRegistryUnavailable() }
        }
        .failIf({ lesson -> checkLessonStarted(lesson.id) }, LessonAlreadyStarted())
}

