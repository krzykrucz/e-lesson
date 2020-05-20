package com.krzykrucz.elesson.currentlesson.domain.preparedness.domain

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.adapters.asyncDoIfRight
import com.krzykrucz.elesson.currentlesson.adapters.asyncFlatMap
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.FindCurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.NotifyStudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.PersistUnpreparedStudentToLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.ReportUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentReportingUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.UnpreparednessError
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier


typealias ReportUnpreparedStudentApi =
    suspend (LessonIdentifier, StudentReportingUnpreparedness) -> Either<UnpreparednessError, Unit>

fun reportUnpreparedStudentApi(
    findCurrentLesson: FindCurrentLesson,
    reportUnpreparedness: ReportUnpreparedness,
    persistUnpreparedStudentToLesson: PersistUnpreparedStudentToLesson,
    notifyStudentMarkedUnprepared: NotifyStudentMarkedUnprepared
): ReportUnpreparedStudentApi = { lessonId, student ->
    findCurrentLesson(lessonId)
        .asyncFlatMap { currentLesson -> reportUnpreparedness(student, currentLesson) }
        .asyncDoIfRight { unpreparedStudent -> persistUnpreparedStudentToLesson(unpreparedStudent) }
        .asyncDoIfRight { unpreparedStudent -> notifyStudentMarkedUnprepared(unpreparedStudent) }
        .map { }
}
