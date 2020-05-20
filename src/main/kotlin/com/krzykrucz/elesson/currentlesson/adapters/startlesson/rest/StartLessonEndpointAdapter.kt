package com.krzykrucz.elesson.currentlesson.adapters.startlesson.rest

import com.krzykrucz.elesson.currentlesson.adapters.asyncDoIfRight
import com.krzykrucz.elesson.currentlesson.domain.shared.FirstName
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.SecondName
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.Teacher
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonStartTime
import com.krzykrucz.elesson.currentlesson.domain.startlesson.PersistStartedLessonIfDoesNotExist
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartLessonError
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
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

@Configuration
class StartLessonRouteAdapter {

    @Bean
    fun startLessonRoute(persistLesson: PersistStartedLessonIfDoesNotExist,
                         startLesson: StartLesson
    ) =
        coRouter {
            POST("/startlesson") { request ->
                val startLessonRequest = request.awaitBody<StartLessonRequest>()
                val teacher = startLessonRequest.toTeacher()
                val now = LessonStartTime.now()

                startLesson(teacher, now)
                    .asyncDoIfRight { persistLesson(it) }
                    .map(StartedLesson::toDto)
                    .fold({
                        val status: HttpStatus = when (it) {
                            is StartLessonError.ClassRegistryUnavailable -> HttpStatus.INTERNAL_SERVER_ERROR
                            else -> HttpStatus.BAD_REQUEST
                        }
                        ServerResponse.status(status)
                            .bodyValueAndAwait(it.javaClass.simpleName)
                    }, {
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValueAndAwait(it)
                    })
            }
        }
}
