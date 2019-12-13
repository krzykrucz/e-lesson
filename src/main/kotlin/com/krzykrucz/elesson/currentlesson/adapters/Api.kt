package com.krzykrucz.elesson.currentlesson.adapters

import com.krzykrucz.elesson.currentlesson.domain.CheckSchedule
import com.krzykrucz.elesson.currentlesson.domain.ClassName
import com.krzykrucz.elesson.currentlesson.domain.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.StartLesson
import com.krzykrucz.elesson.currentlesson.domain.StartLessonError
import com.krzykrucz.elesson.currentlesson.domain.startLesson
import com.virtuslab.basetypes.result.arrow.AsyncResult
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

data class StartLessonRequest(
    val teacher: String
)

data class LessonIdentifier(val date: LocalDate,
                            val lessonHourNumber: LessonHourNumber,
                            val className: ClassName
)

data class StudentResponse(val name: String)
data class ClassRegistryResponse(
    val lessonId: LessonIdentifier,
    val students: List<StudentResponse>
)

typealias StartLessonApi = (StartLessonRequest) -> AsyncResult<ClassRegistryResponse, StartLessonError>

fun startLessonApi(
    persistStartedLesson: PersistStartedLesson,
    startLesson: StartLesson
): StartLessonApi = {
    TODO()
}

@Configuration
class ApiConfig {
    @Bean
    fun api(
        persistStartedLesson: PersistStartedLesson,
        checkSchedule: CheckSchedule,
        fetchClassRegistry: FetchClassRegistry
    ): StartLessonApi = TODO()
}