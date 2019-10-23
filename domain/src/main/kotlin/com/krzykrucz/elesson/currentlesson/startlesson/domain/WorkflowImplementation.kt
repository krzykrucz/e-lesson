package com.krzykrucz.elesson.currentlesson.startlesson.domain

import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.failIf
import com.krzykrucz.elesson.currentlesson.shared.flatMapSuccess
import com.krzykrucz.elesson.currentlesson.shared.mapError
import com.krzykrucz.elesson.currentlesson.shared.mapSuccess
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartLessonError.ClassRegistryUnavailable
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartLessonError.NotScheduledLesson


private fun ScheduledLesson.lessonIdentifier() =
        LessonIdentifier(this.scheduledTime.toLocalDate(), this.lessonHourNumber, this.className)

private fun ScheduledLesson.toCurrentLessonWithClass(classRegistry: ClassRegistry) =
        StartedLesson(this.lessonIdentifier(), classRegistry)


fun startLesson(checkScheduledLesson: CheckScheduledLesson,
                fetchClassRegistry: FetchClassRegistry): StartLesson = { teacher, attemptedStartTime ->
    checkScheduledLesson(teacher, attemptedStartTime)
        .mapError { NotScheduledLesson() }
        .failIf({ scheduledLesson -> attemptedStartTime.isBefore(scheduledLesson.scheduledTime) }, NotScheduledLesson())
        .failIf({ scheduledLesson -> attemptedStartTime.isAfter(scheduledLesson.scheduledTime.plusMinutes(44)) }, NotScheduledLesson())
        // ^ TODO maybe enclose these 2 mapping in the above and introduce another internal error type (lesson started too early or so)
        // ^ TODO extract a separate function with invariant
        .flatMapSuccess { scheduledLesson ->
            fetchClassRegistry(scheduledLesson.className)
                .mapSuccess(scheduledLesson::toCurrentLessonWithClass)
                .mapError { ClassRegistryUnavailable() }
        }
}

