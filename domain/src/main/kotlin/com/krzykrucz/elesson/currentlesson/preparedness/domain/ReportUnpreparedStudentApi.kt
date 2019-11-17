package com.krzykrucz.elesson.currentlesson.preparedness.domain

import arrow.core.Tuple2
import arrow.core.right
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.FindCurrentLesson
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.NotifyStudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.PersistUnpreparedStudentToLesson
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.ReportUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.StudentReportingUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.UnpreparednessError
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.flatMapAsyncSuccess
import com.krzykrucz.elesson.currentlesson.shared.mapSuccess


typealias ReportUnpreparedStudentApi =
    (LessonIdentifier, StudentReportingUnpreparedness) -> AsyncOutput<UnpreparednessError, Unit>

fun reportUnpreparedStudentApi(
    findCurrentLesson: FindCurrentLesson,
    reportUnpreparedness: ReportUnpreparedness,
    persistUnpreparedStudentToLesson: PersistUnpreparedStudentToLesson,
    notifyStudentMarkedUnprepared: NotifyStudentMarkedUnprepared
): ReportUnpreparedStudentApi = { lessonId, student ->
    findCurrentLesson(lessonId)
        .mapSuccess { Tuple2(it, student) }
        .flatMapAsyncSuccess { reportUnpreparedness(it.b, it.a) }
        .flatMapAsyncSuccess { event ->
            persistUnpreparedStudentToLesson(event)
                .map { event.right() }
        }
        .flatMapAsyncSuccess { notifyStudentMarkedUnprepared(it).map { it.right() } }
}