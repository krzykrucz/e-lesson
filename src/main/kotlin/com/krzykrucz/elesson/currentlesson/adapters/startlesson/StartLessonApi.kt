package com.krzykrucz.elesson.currentlesson.adapters.startlesson

import com.krzykrucz.elesson.currentlesson.adapters.RestApi
import com.krzykrucz.elesson.currentlesson.adapters.asyncDoIfRight
import com.krzykrucz.elesson.currentlesson.adapters.toServerResponse
import com.krzykrucz.elesson.currentlesson.domain.shared.FirstName
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.SecondName
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.Teacher
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FetchScheduledLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.PersistStartedLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.startLessonWorkflow
import org.springframework.web.reactive.function.server.awaitBody
import java.time.LocalDateTime
import javax.validation.constraints.NotEmpty


fun startLessonApi(
    fetchScheduledLesson: FetchScheduledLesson,
    fetchClassRegistry: FetchClassRegistry,
    persistStartedLesson: PersistStartedLesson,
    startLesson: StartLesson = startLessonWorkflow(fetchScheduledLesson, fetchClassRegistry)
): RestApi = { request ->
    request.awaitBody<StartLessonRequest>()
        .toTeacher()
        .let { teacher -> startLesson(teacher, LocalDateTime.now()) }
        .asyncDoIfRight { persistStartedLesson(it) }
        .map(StartedLesson::toDto)
        .toServerResponse()
}

private data class StudentResponse(val name: String)

private data class ClassRegistryResponse(
    val lessonId: LessonIdentifier,
    val students: List<StudentResponse>
)

private data class StartLessonRequest(
    @NotEmpty val teacherFirstName: String,
    @NotEmpty val teacherSecondName: String
) {
    fun toTeacher() =
        Teacher(FirstName(NonEmptyText.of(teacherFirstName)!!), SecondName(NonEmptyText.of(teacherSecondName)!!))
}

private fun StartedLesson.toDto() =
    this.clazz.students
        .map { StudentResponse("${it.firstName.name.text} ${it.secondName.name.text}") }
        .let { ClassRegistryResponse(this.id, it) }
