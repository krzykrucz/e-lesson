package com.krzykrucz.elesson.currentlesson.preparedness

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.asyncDoIfRight
import com.krzykrucz.elesson.currentlesson.shared.asyncFlatMap


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
