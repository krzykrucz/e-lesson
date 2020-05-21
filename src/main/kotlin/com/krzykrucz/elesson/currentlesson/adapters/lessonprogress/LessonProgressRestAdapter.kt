package com.krzykrucz.elesson.currentlesson.adapters.lessonprogress

import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class LessonProgressRestAdapter {

    @Bean
    fun lessonProgressRouter(loadLessonProgress: LoadLessonProgress) = coRouter {
        GET("/progress", handleLessonProgressViewRequest(loadLessonProgress))
    }
}

data class LessonProgressDto(
    val semester: Int,
    val className: String,
    val subject: String,
    val date: String,
    val topic: LessonTopic?,
    val status: String
) {
    companion object {
        fun fromLessonProgress(lessonProgress: LessonProgress): LessonProgressDto =
            LessonProgressDto(
                semester = lessonProgress.semester.semesterOrdinalNumber.number,
                className = lessonProgress.className.name.text,
                subject = lessonProgress.subject.subject.text,
                date = lessonProgress.date.toString(),
                status = lessonProgress.status.status,
                topic = lessonProgress.topic.orNull()
            )
    }
}
