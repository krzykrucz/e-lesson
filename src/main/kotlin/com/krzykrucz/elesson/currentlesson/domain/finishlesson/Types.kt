package com.krzykrucz.elesson.currentlesson.domain.finishlesson

import com.krzykrucz.elesson.currentlesson.domain.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import java.time.LocalTime

enum class LessonBell {
    RANG, NOT_RANG
}

data class FinishedLesson private constructor(val lessonTopic: LessonTopic, val lessonOrdinalInSemester: LessonOrdinalInSemester) {
    constructor(lessonTopic: LessonTopic) : this(lessonTopic, lessonTopic.lessonOrdinalInSemester)
}

sealed class FinishLessonError {
    data class BellNotRang(val error: String = "Bell did not ring") : FinishLessonError()
    data class LessonNotFound(val error: String = "Could not find a lesson"): FinishLessonError()
}

typealias FinishLessonTime = LocalTime
