package com.krzykrucz.elesson.currentlesson.adapters

import arrow.core.getOrElse
import com.krzykrucz.elesson.currentlesson.domain.AttemptedLessonStartTime
import com.krzykrucz.elesson.currentlesson.domain.CheckSchedule
import com.krzykrucz.elesson.currentlesson.domain.ClassName
import com.krzykrucz.elesson.currentlesson.domain.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.StartLesson
import com.krzykrucz.elesson.currentlesson.domain.StartLessonError
import com.krzykrucz.elesson.currentlesson.domain.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.Teacher
import com.krzykrucz.elesson.currentlesson.domain.startLesson
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.arrow.AsyncResult
import com.virtuslab.basetypes.result.arrow.flatMapSuccess
import com.virtuslab.basetypes.result.arrow.liftMap
import com.virtuslab.basetypes.result.arrow.mapFailure
import com.virtuslab.basetypes.result.arrow.mapSuccess
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.LocalDateTime

data class StartLessonRequest(
    val teacher: String
) {
    // TODO use this:
    fun toTeacher(): Result<Teacher, StartLessonError.WrongTeacher> =
        NonEmptyText.of(teacher)
            .map(::Teacher)
            .map { Result.success(it) }
            .getOrElse { Result.error(StartLessonError.WrongTeacher) }
}

data class LessonIdentifier(val id: String)

data class StudentResponse(val name: String)
data class ClassRegistryResponse(
    val lessonId: LessonIdentifier,
    val students: List<StudentResponse>
)

fun StartedLesson.toClassRegistryResponse(identifier: LessonIdentifier) =
    ClassRegistryResponse(
        identifier,
        classRegistry.studentList
            .map { record -> "${record.firstName.text} ${record.secondName.text}" }
            .map(::StudentResponse)
            .toList()
    )

typealias StartLessonApi = (StartLessonRequest) -> AsyncResult<ClassRegistryResponse, StartLessonError>

fun startLessonApi(
    persistStartedLesson: PersistStartedLesson,
    startLesson: StartLesson
): StartLessonApi = {
    it.toTeacher()
        .liftMap { teacher ->
            startLesson(
                teacher,
                AttemptedLessonStartTime(LocalDateTime.now())
            )
        }
        .flatMapSuccess { lesson ->
            persistStartedLesson(lesson)
                .mapSuccess { lesson to it }
                .mapFailure { StartLessonError.ExternalError }
        }.mapSuccess { (startedLesson, identifier) ->
            startedLesson.toClassRegistryResponse(identifier)
        }
}

@Configuration
class ApiConfig {
    @Bean
    fun api(
        persistStartedLesson: PersistStartedLesson,
        checkSchedule: CheckSchedule,
        fetchClassRegistry: FetchClassRegistry
    ): StartLessonApi =
        startLessonApi(
            persistStartedLesson,
            startLesson(
                checkSchedule,
                fetchClassRegistry
            )
        )
}