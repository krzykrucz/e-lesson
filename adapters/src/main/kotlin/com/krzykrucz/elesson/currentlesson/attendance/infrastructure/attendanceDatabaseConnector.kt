package com.krzykrucz.elesson.currentlesson.attendance.infrastructure

import arrow.effects.IO
import arrow.effects.extensions.io.functor.unit
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.GetLessonStartTime
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.ATTENDANCE_DATABASE
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.CLASS_REGISTRY_DATABASE
import com.krzykrucz.elesson.currentlesson.infrastructure.Database.Companion.SCHEDULE

typealias FetchClassRegistry = (ClassName) -> IO<ClassRegistry>
typealias PersistCheckedAttendance = (LessonIdentifier, CheckedAttendance) -> IO<Unit>


fun fetchClassRegistry(): FetchClassRegistry = { className ->
    IO.just(CLASS_REGISTRY_DATABASE[className]!!)
}

fun getLessonStartTime(): GetLessonStartTime = { lessonHourNumber ->
    SCHEDULE[lessonHourNumber]!!
}

fun persistCheckedAttendance(): PersistCheckedAttendance = { lessonIdentifier, checkedAttendance ->
    IO.just(
            ATTENDANCE_DATABASE.put(lessonIdentifier, checkedAttendance)
    ).unit()
}