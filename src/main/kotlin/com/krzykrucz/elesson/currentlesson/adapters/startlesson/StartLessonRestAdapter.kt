package com.krzykrucz.elesson.currentlesson.adapters.startlesson.rest

import com.krzykrucz.elesson.currentlesson.adapters.asyncDoIfRight
import com.krzykrucz.elesson.currentlesson.adapters.toServerResponse
import com.krzykrucz.elesson.currentlesson.domain.shared.FirstName
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.SecondName
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.Teacher
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonStartTime
import com.krzykrucz.elesson.currentlesson.domain.startlesson.PersistStartedLessonIfDoesNotExist
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartLesson
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter
import javax.validation.constraints.NotEmpty


internal fun startLessonRestAdapter(persistLesson: PersistStartedLessonIfDoesNotExist,
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
                .toServerResponse()
        }
    }


private data class StudentResponse(val name: String)

private data class ClassRegistryResponse(
    val lessonId: LessonIdentifier,
    val students: List<StudentResponse>
)

private data class StartLessonRequest(
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

