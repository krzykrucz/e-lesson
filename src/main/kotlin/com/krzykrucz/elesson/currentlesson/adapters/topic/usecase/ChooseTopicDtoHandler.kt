package com.krzykrucz.elesson.currentlesson.adapters.topic.usecase

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.adapters.asyncMap
import com.krzykrucz.elesson.currentlesson.adapters.topic.ChooseTopicDto
import com.krzykrucz.elesson.currentlesson.adapters.topic.usecase.persistence.checkIfAttendanceIsChecked
import com.krzykrucz.elesson.currentlesson.adapters.topic.usecase.persistence.fetchFinishedLessonsCount
import com.krzykrucz.elesson.currentlesson.adapters.topic.usecase.persistence.persistInProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.topic.ChooseTopicError
import com.krzykrucz.elesson.currentlesson.domain.topic.chooseTopic


val handleChooseTopicDto: suspend (ChooseTopicDto) -> Either<ChooseTopicError, Unit> = { chooseTopicDto ->
    val isAttendanceChecked = checkIfAttendanceIsChecked(chooseTopicDto.lessonIdentifier)
    val finishedLessonsCount = fetchFinishedLessonsCount()
    chooseTopic(
        isAttendanceChecked,
        chooseTopicDto.topicTitle,
        finishedLessonsCount,
        chooseTopicDto.lessonIdentifier
    )
        .asyncMap { inProgressLesson -> persistInProgressLesson(chooseTopicDto.lessonIdentifier, inProgressLesson) }
}
