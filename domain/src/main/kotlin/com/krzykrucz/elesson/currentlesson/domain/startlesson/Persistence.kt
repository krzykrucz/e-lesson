package com.krzykrucz.elesson.currentlesson.domain.startlesson


typealias PersistLesson = (LessonBeforeAttendance) -> LessonIdentifier

typealias QueryLessonBeforeAttendance = (LessonIdentifier) -> LessonBeforeAttendance