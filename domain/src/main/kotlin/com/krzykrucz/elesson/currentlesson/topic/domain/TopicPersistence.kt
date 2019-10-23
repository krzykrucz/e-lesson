package com.krzykrucz.elesson.currentlesson.domain.topic.domain

import arrow.fx.IO

typealias CountFinishLessons = () -> IO<FinishedLessonsCount>
typealias PersistInProgressLesson = (InProgressLesson) -> IO<Unit>