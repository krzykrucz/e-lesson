package com.krzykrucz.elesson.currentlesson.topic.usecase

import arrow.core.Either
import arrow.core.extensions.either.traverse.sequence
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix
import com.krzykrucz.elesson.currentlesson.topic.ChooseTopicDto
import com.krzykrucz.elesson.currentlesson.topic.domain.ChooseTopicError
import com.krzykrucz.elesson.currentlesson.topic.domain.chooseTopic
import com.krzykrucz.elesson.currentlesson.topic.usecase.persistence.checkIfAttendanceIsChecked
import com.krzykrucz.elesson.currentlesson.topic.usecase.persistence.fetchFinishedLessonsCount
import com.krzykrucz.elesson.currentlesson.topic.usecase.persistence.persistInProgressLesson
import java.time.LocalDate


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

private fun <A, B> Either<A, IO<B>>.sequence(): IO<Either<A, B>> =
    this.sequence(IO.applicative()).fix()
        .map { it.fix() }
