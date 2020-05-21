package com.krzykrucz.elesson.currentlesson.adapters.topic


import com.krzykrucz.elesson.currentlesson.adapters.asyncDoIfRight
import com.krzykrucz.elesson.currentlesson.adapters.toServerResponse
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.TopicTitle
import com.krzykrucz.elesson.currentlesson.domain.topic.ChooseTopic
import com.krzykrucz.elesson.currentlesson.domain.topic.PersistInProgressLesson
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
