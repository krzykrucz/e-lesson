package com.krzykrucz.elesson.currentlesson.lessonprogress

import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.shared.Semester
import java.time.LocalDate

sealed class LessonStatus(val status: String)
object Scheduled : LessonStatus("Scheduled")
object InProgress : LessonStatus("In Progress")
object Finished : LessonStatus("Finished")


typealias LessonDate = LocalDate

data class LessonProgress(
    val semester: Semester,
    val className: ClassName,
    val subject: LessonSubject,
    val date: LessonDate,
    val topic: Option<LessonTopic>,
    val status: LessonStatus
)
