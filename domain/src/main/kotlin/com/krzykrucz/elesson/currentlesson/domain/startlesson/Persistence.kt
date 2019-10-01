package com.krzykrucz.elesson.currentlesson.domain.startlesson


typealias PersistLesson = (StartedLesson) -> LessonIdentifier

typealias QueryLessonBeforeAttendance = (LessonIdentifier) -> StartedLesson