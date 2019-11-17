package com.krzykrucz.elesson.currentlesson.finishlesson.domain

import com.krzykrucz.elesson.currentlesson.shared.LessonOrdinalNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic
import java.time.LocalTime

enum class LessonBell {
    RANG, NOT_RANG
}

data class FinishedLesson private constructor(val lessonTopic: LessonTopic, val lessonOrdinalNumber: LessonOrdinalNumber) {
    constructor(lessonTopic: LessonTopic) : this(lessonTopic, lessonTopic.lessonOrdinalNumber)
}

sealed class FinishLessonError {
    data class BellNotRang(val error: String = "Bell did not ring") : FinishLessonError()
    data class LessonNotFound(val error: String = "Could not find a lesson"): FinishLessonError()
}

typealias FinishLessonTime = LocalTime
