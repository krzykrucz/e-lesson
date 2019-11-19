package com.krzykrucz.elesson.currentlesson.adapters.topic.usecase

import arrow.core.Either
import arrow.fx.IO
import arrow.fx.extensions.fx
import com.krzykrucz.elesson.currentlesson.adapters.sequence
import com.krzykrucz.elesson.currentlesson.adapters.topic.ChooseTopicDto
import com.krzykrucz.elesson.currentlesson.adapters.topic.usecase.persistence.checkIfAttendanceIsChecked
import com.krzykrucz.elesson.currentlesson.adapters.topic.usecase.persistence.fetchFinishedLessonsCount
import com.krzykrucz.elesson.currentlesson.adapters.topic.usecase.persistence.persistInProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.topic.ChooseTopicError
import com.krzykrucz.elesson.currentlesson.domain.topic.chooseTopic


fun handleChooseTopicDto(): (ChooseTopicDto) -> IO<Either<ChooseTopicError, Unit>> = { chooseTopicDto ->
    IO.fx {
        val (isAttendanceChecked) = checkIfAttendanceIsChecked()(chooseTopicDto.lessonIdentifier)
        val (finishedLessonsCount) = fetchFinishedLessonsCount()()
        val (persistedLessonOrError) = chooseTopic()(
            isAttendanceChecked,
            chooseTopicDto.topicTitle,
            finishedLessonsCount,
            chooseTopicDto.lessonIdentifier
        )
            .map { inProgressLesson -> persistInProgressLesson()(chooseTopicDto.lessonIdentifier, inProgressLesson) }
            .sequence()
        persistedLessonOrError
    }
}
