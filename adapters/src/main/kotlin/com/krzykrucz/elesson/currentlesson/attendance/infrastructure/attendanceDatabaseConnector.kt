package com.krzykrucz.elesson.currentlesson.attendance.infrastructure

import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.GetLessonStartTime
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.ATTENDANCE_DATABASE
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.CLASS_REGISTRY_DATABASE
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.SCHEDULE

typealias FetchClassRegistry = (ClassName) -> ClassRegistry
typealias PersistCheckedAttendance = (LessonIdentifier, CheckedAttendance) -> Unit


fun fetchClassRegistry(): FetchClassRegistry = { className ->
    CLASS_REGISTRY_DATABASE[className]!!
}

fun getLessonStartTime(): GetLessonStartTime = { lessonHourNumber ->
    SCHEDULE[lessonHourNumber]!!
}

fun persistCheckedAttendance(): PersistCheckedAttendance = { lessonIdentifier, checkedAttendance ->
    ATTENDANCE_DATABASE[lessonIdentifier] = checkedAttendance
}