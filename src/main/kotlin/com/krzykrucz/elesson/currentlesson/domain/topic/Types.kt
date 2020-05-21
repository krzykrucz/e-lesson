package com.krzykrucz.elesson.currentlesson.domain.topic

data class FinishedLessonsCount(val count: Int)

sealed class ChooseTopicError {
    data class AttendanceNotChecked(val error: String = "Cannot choose topic for lesson that does not have attendance checked") : ChooseTopicError()
}

