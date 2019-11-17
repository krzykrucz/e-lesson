package com.krzykrucz.elesson.currentlesson.finishlesson.domain

import com.krzykrucz.elesson.currentlesson.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic
import java.time.LocalTime

enum class LessonBell {
    RANG, NOT_RANG
}

data class FinishedLesson private constructor(val lessonTopic: LessonTopic, val lessonOrdinalInSemester: LessonOrdinalInSemester) {
    constructor(lessonTopic: LessonTopic) : this(lessonTopic, lessonTopic.lessonOrdinalInSemester)
}

sealed class FinishLessonError {
    data class BellNotRang(val error: String = "Bell did not ring") : FinishLessonError()
}

typealias FinishLessonTime = LocalTime
