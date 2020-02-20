package com.krzykrucz.elesson.currentlesson.adapters

import arrow.core.getOrElse
import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.domain.*
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
    fun toTeacher(): Result<Teacher, StartingLessonFailure.WrongTeacher> =
        teacher.split(" ")
            .toOption()
            .filter { it.size == 2 }
            .map { name -> Teacher(FirstName(NonEmptyText.of(name[0]).orNull()!!), SecondName(NonEmptyText.of(name[1]).orNull()!!)) }
            .map { Result.success(it) }
            .getOrElse { Result.error(StartingLessonFailure.WrongTeacher) }
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
        pupilRegister.pupils
            .map { record -> "${record.firstName.name} ${record.secondName.name}" }
            .map(::StudentResponse)
            .toList()
    )

typealias StartLessonApi = (StartLessonRequest) -> AsyncResult<ClassRegistryResponse, StartingLessonFailure>

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
                .mapFailure { StartingLessonFailure.ExternalError }
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
                fetchClassRegistry,
                checkTime
            )
        )
}