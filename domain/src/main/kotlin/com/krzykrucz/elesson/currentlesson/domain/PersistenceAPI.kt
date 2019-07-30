package com.krzykrucz.elesson.currentlesson.domain


typealias PersistLesson = (CurrentLesson) -> LessonIdentifier

typealias QueryCurrentLesson = (LessonIdentifier) -> CurrentLesson