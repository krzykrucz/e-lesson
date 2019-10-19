package com.krzykrucz.elesson.currentlesson.monolith

import arrow.core.andThen
import arrow.core.toOption
import arrow.data.OptionT
import arrow.data.fix
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.applicative.applicative
import arrow.effects.extensions.io.functor.functor
import arrow.effects.extensions.io.monad.monad
import com.krzykrucz.elesson.currentlesson.attendance.domain.Attendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.AttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchStartedLesson
import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchStartedLessonAsAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.NotCompletedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.UncheckedStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.lessonId
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.StudentRecord
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartedLesson
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

class Database {

    companion object {
        private val lessonId1 = lessonIdOf("2019-09-09", 1, "1A")

        private val classRegistryOf1A = ClassRegistry(
                students = listOf(
                        createStudentRecord("Harry", "Potter", 1),
                        createStudentRecord("Tom", "Riddle", 2)

                ),
                className = classNameOf("1A")
        )

        val STARTED_LESSON_DATABASE: ConcurrentHashMap<LessonIdentifier, StartedLesson> = ConcurrentHashMap(mutableMapOf(
                lessonId1 to startedLessonOf(lessonId1)
        ))

        val ATTENDANCE_DATABASE: ConcurrentHashMap<LessonIdentifier, Attendance> = ConcurrentHashMap(mutableMapOf(
        ))

        private fun startedLessonOf(lessonIdentifier: LessonIdentifier): StartedLesson =
                StartedLesson(
                        lessonIdentifier,
                        classRegistryOf1A
                )

        fun lessonIdOf(date: String, number: Int, className: String) =
                LessonIdentifier(LocalDate.parse(date), lessonHourNumberOf(number), classNameOf(className))

        private fun lessonHourNumberOf(number: Int) =
                LessonHourNumber.of(number).orNull()!!

        private fun classNameOf(name: String) =
                ClassName(NonEmptyText.of(name)!!)

        private fun createStudentRecord(name: String, surname: String, numberInRegister: Int): StudentRecord =
                StudentRecord(
                        firstName = FirstName(NonEmptyText.of(name)!!),
                        secondName = SecondName(NonEmptyText.of(surname)!!),
                        numberInRegister = NaturalNumber.of(numberInRegister)
                                .map(::NumberInRegister)
                                .orNull()!!
                )

    }

}

private fun fetchAttendance(): FetchAttendance = { lessonIdentifier ->
    OptionT.fromOption(IO.applicative(), Database.ATTENDANCE_DATABASE[lessonIdentifier].toOption())
}

fun fetchStartedLesson(): FetchStartedLesson = { lessonIdentifier ->
    OptionT.fromOption(IO.applicative(), Database.STARTED_LESSON_DATABASE[lessonIdentifier].toOption())
}

fun fetchStartedLessonAsAttendance(): FetchStartedLessonAsAttendance =
        fetchStartedLesson().andThen { startedLessonIo ->
            startedLessonIo.map(IO.functor()) { it.toNotCompletedAttendance() }
        }

fun fetchClassRegistry(): FetchClassRegistry = { attendance ->
    fetchStartedLesson()(attendance.lessonId())
            .map(IO.functor()) { it.clazz }
}

fun getClassRegistry(attendanceAndStudent: OptionT<ForIO, AttendanceAndStudentDto>): OptionT<ForIO, NoteStudentDto> =
        attendanceAndStudent
                .flatMap(IO.monad()) {
                    fetchClassRegistry()(it.notCompletedAttendance)
                            .map(IO.functor()) { classRegistry ->
                                NoteStudentDto(it.uncheckedStudent, it.notCompletedAttendance, classRegistry)
                            }
                }

fun fetchCheckedAttendance(lessonId: LessonIdentifier): OptionT<ForIO, CheckedAttendance> =
        fetchAttendance()(lessonId)
                .map(IO.functor()) { it as CheckedAttendance }

fun fetchNotCompletedAttendance(lessonId: LessonIdentifier, student: UncheckedStudent): OptionT<ForIO, AttendanceAndStudentDto> =
        fetchAttendance()(lessonId)
                .map(IO.functor()) { it as NotCompletedAttendance }
                .orElse(IO.monad()) { fetchStartedLessonAsAttendance()(lessonId) }
                .map(IO.functor()) { AttendanceAndStudentDto(student, it) }
                .fix()


data class AttendanceAndStudentDto(
        val uncheckedStudent: UncheckedStudent,
        val notCompletedAttendance: NotCompletedAttendance
)

data class NoteStudentDto(
        val uncheckedStudent: UncheckedStudent,
        val notCompletedAttendance: NotCompletedAttendance,
        val classRegistry: ClassRegistry
)


fun StartedLesson.toNotCompletedAttendance() = NotCompletedAttendance(attendance = AttendanceList(
        className = this.id.className,
        date = this.id.date,
        lessonHourNumber = this.id.lessonHourNumber
))