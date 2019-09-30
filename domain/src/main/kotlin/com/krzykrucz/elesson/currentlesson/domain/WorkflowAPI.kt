package com.krzykrucz.elesson.currentlesson.domain

import java.time.LocalDateTime


typealias CheckScheduledLesson = (Teacher, LocalDateTime) -> AsyncOutput<ScheduledLesson, Throwable>
typealias FetchClassRegistry = (ClassName) -> AsyncOutput<ClassRegistry, Throwable>
typealias CheckLessonStarted = (LessonIdentifier) -> Boolean

typealias AttemptedLessonStartTime = LocalDateTime
typealias StartLesson = (Teacher, AttemptedLessonStartTime) -> AsyncOutput<LessonBeforeAttendance, StartLessonError>
typealias NoteStudentPresence = (Student) -> Async<StudentWithCheckedPresence>
typealias CheckIfAllStudentsAreNoted = (List<Student>, Attendance) -> Output<AttendanceCheckFinished, StartLessonError>
typealias CheckAttendance = (LessonBeforeAttendance) -> AsyncOutput<LessonBeforeTopic, StartLessonError>
