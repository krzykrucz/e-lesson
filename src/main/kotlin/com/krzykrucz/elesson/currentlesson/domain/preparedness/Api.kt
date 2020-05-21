package com.krzykrucz.elesson.currentlesson.domain.preparedness

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.adapters.asyncDoIfRight
import com.krzykrucz.elesson.currentlesson.adapters.asyncFlatMap
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier


typealias ReportUnpreparedStudentApi =
    suspend (LessonIdentifier, StudentReportingUnpreparedness) -> Either<UnpreparednessError, Unit>

fun reportUnpreparedStudentApi(
    findCurrentLesson: FindCurrentLesson,
    persistUnpreparedStudentToLesson: PersistUnpreparedStudentToLesson,
    notifyStudentMarkedUnprepared: NotifyStudentMarkedUnprepared,
    getStudentSubjectUnpreparednessInASemester: GetStudentSubjectUnpreparednessInASemester,
    reportUnpreparedness: ReportUnpreparedness = reportUnpreparednessWorkflow(getStudentSubjectUnpreparednessInASemester)
): ReportUnpreparedStudentApi = { lessonId, student ->
    findCurrentLesson(lessonId)
        .asyncFlatMap { currentLesson -> reportUnpreparedness(student, currentLesson) }
        .asyncDoIfRight { unpreparedStudent -> persistUnpreparedStudentToLesson(unpreparedStudent) }
        .asyncDoIfRight { unpreparedStudent -> notifyStudentMarkedUnprepared(unpreparedStudent) }
        .map { }
}
