package com.krzykrucz.elesson.currentlesson.domain.preparedness

import com.krzykrucz.elesson.currentlesson.domain.shared.FirstName
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.SecondName
import java.time.LocalDateTime


data class UnpreparedStudent(
    val firstName: FirstName,
    val secondName: SecondName
)

data class StudentsUnpreparedForLesson(val students: List<UnpreparedStudent> = emptyList())

sealed class UnpreparednessError {
    object AlreadyRaised : UnpreparednessError()
    object UnpreparedTooManyTimes : UnpreparednessError()
    object TooLateToRaiseUnpreparedness : UnpreparednessError()
    object StudentNotPresent : UnpreparednessError()
    object LessonNotStarted: UnpreparednessError()
}

data class StudentReportingUnpreparedness(
        val firstName: String,
        val secondName: String
)

//event
data class StudentMarkedUnprepared(
    val lessonId: LessonIdentifier,
    val happenedAt: LocalDateTime = LocalDateTime.now(),
    val unpreparedStudent: UnpreparedStudent,
    val studentsUnpreparedForLesson: StudentsUnpreparedForLesson
)
