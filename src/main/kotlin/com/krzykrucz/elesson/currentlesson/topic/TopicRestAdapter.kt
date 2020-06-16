package com.krzykrucz.elesson.currentlesson.topic


import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.TopicTitle
import com.krzykrucz.elesson.currentlesson.shared.asyncDoIfRight
import com.krzykrucz.elesson.currentlesson.shared.toServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter


internal fun topicRestAdapter(
    chooseTopic: ChooseTopic,
    persistInProgressLesson: PersistInProgressLesson
) = coRouter {
    POST("/topic") { request ->
        val (id, title) = request.awaitBody<ChooseTopicDto>()
        chooseTopic(title, id)
            .asyncDoIfRight { inProgressLesson -> persistInProgressLesson(id, inProgressLesson) }
            .toServerResponse()
    }
}

private data class ChooseTopicDto(
    val lessonIdentifier: LessonIdentifier,
    val topicTitle: TopicTitle
)
