package com.krzykrucz.elesson.currentlesson.startlesson.adapters.rest

import arrow.core.getOrHandle
import com.krzykrucz.elesson.currentlesson.MonoDomainError
import com.krzykrucz.elesson.currentlesson.handleErrors
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.shared.Teacher
import com.krzykrucz.elesson.currentlesson.startlesson.domain.AttemptedLessonStartTime
import com.krzykrucz.elesson.currentlesson.startlesson.domain.PersistStartedLessonIfDoesNotExist
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartLesson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import javax.validation.constraints.NotEmpty

data class StudentResponse(val name: String)

data class ClassRegistryResponse(
    val lessonId: LessonIdentifier,
    val students: List<StudentResponse>
)

data class StartLessonRequest(
    @NotEmpty
    val teacherFirstName: String,
    @NotEmpty
    val teacherSecondName: String
) {
    fun toTeacher() =
            Teacher(
                    FirstName(
                            NonEmptyText.of(teacherFirstName)!!
                    ),
                    SecondName(
                            NonEmptyText.of(teacherSecondName)!!
                    )
            )
}

private fun StartedLesson.toDto() =
    this.clazz.students
        .map { StudentResponse("${it.firstName.name.text} ${it.secondName.name.text}") }
        .let { ClassRegistryResponse(this.id, it) }

fun startLessonToMono(startLessonRequest: StartLessonRequest, startLesson: StartLesson): Mono<StartedLesson> =
    startLesson(startLessonRequest.toTeacher(), AttemptedLessonStartTime.now())
        .unsafeRunSync()
        .map { Mono.just(it) }
        .getOrHandle { Mono.error(MonoDomainError(it)) }

fun persistLessonToMono(startedLesson: StartedLesson, persistLesson: PersistStartedLessonIfDoesNotExist): Mono<StartedLesson> =
    persistLesson(startedLesson)
        .unsafeRunSync()
        .let { Mono.just(startedLesson) }

@Configuration
class StartLessonRouteAdapter {

    @Bean
    fun startLessonRoute(persistLesson: PersistStartedLessonIfDoesNotExist,
              startLesson: StartLesson) =
        router {
            POST("/startlesson") { request ->
                request.bodyToMono(StartLessonRequest::class.java)
                    .flatMap { startLessonToMono(it, startLesson) }
                    .flatMap { persistLessonToMono(it, persistLesson) }
                    .map { it.toDto() }
                    .flatMap {
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromObject(it))
                    }
                    .handleErrors()
            }
        }
}