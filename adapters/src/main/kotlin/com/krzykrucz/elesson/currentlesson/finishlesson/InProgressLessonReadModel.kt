package com.krzykrucz.elesson.currentlesson.finishlesson

import arrow.core.Option
import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.monolith.Database
import com.krzykrucz.elesson.currentlesson.shared.InProgress
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

fun readInProgressLesson(id: LessonIdentifier): Option<InProgressLesson> =
    Database.LESSON_DATABASE[id].toOption()
        .filter { lesson -> lesson.status == InProgress }
        .flatMap { lesson -> lesson.lessonTopic }
        .map { topic -> InProgressLesson(lessonTopic = topic) }
