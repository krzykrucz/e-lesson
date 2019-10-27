package com.krzykrucz.elesson.currentlesson.lessonprogress.adapters.persistence

import arrow.core.Either
import arrow.core.extensions.either.traverse.sequence
import arrow.core.fix
import arrow.core.toOption
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix
import com.krzykrucz.elesson.currentlesson.lessonprogress.CreateLessonProgressView
import com.krzykrucz.elesson.currentlesson.lessonprogress.FetchLessonProgress
import com.krzykrucz.elesson.currentlesson.lessonprogress.LessonProgress
import com.krzykrucz.elesson.currentlesson.lessonprogress.LessonProgressError
import com.krzykrucz.elesson.currentlesson.monolith.Database

fun fetchLessonProgress(): FetchLessonProgress = { lessonIdentifier ->
    Database.LESSON_PROGRESS_VIEW[lessonIdentifier].toOption()
        .toEither(ifEmpty = { LessonProgressError.LessonNotFound() } )
        .let { IO.just(it) }
        .flatMap { lessonProgressOrError ->
            if (lessonProgressOrError.isLeft()) createLessonProgressView()(lessonIdentifier)
            else IO.just(lessonProgressOrError)
        }
}

fun createLessonProgressView(): CreateLessonProgressView = { lessonIdentifier ->
    val lessonProgressView = Database.LESSON_DATABASE[lessonIdentifier].toOption()
        .map { currentLesson ->
            LessonProgress(
                semester = currentLesson.semester,
                className = currentLesson.classRegistry.className,
                date = currentLesson.lessonId.date,
                subject = currentLesson.subject,
                topic = currentLesson.lessonTopic,
                status = currentLesson.status
            )
        }
        .toEither(ifEmpty = { LessonProgressError.LessonNotFound() } )
        .let { IO.just(it) }

    IO.fx {
        val (lessonProgressOrError) = lessonProgressView
        val (savedLessonProgressView) = lessonProgressOrError.map { lessonProgress ->
            IO {
                Database.LESSON_PROGRESS_VIEW[lessonIdentifier] = lessonProgress
                lessonProgress
            }
        }.sequence()
        savedLessonProgressView
    }
}

private fun Either<LessonProgressError, IO<LessonProgress>>.sequence(): IO<Either<LessonProgressError, LessonProgress>> =
    this.sequence(IO.applicative()).fix()
        .map { it.fix() }
