package com.krzykrucz.elesson.currentlesson.adapters.topic


import com.krzykrucz.elesson.currentlesson.adapters.asyncDoIfRight
import com.krzykrucz.elesson.currentlesson.adapters.toServerResponse
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.TopicTitle
import com.krzykrucz.elesson.currentlesson.domain.topic.CheckIfAttendanceIsChecked
import com.krzykrucz.elesson.currentlesson.domain.topic.CountFinishedLessons
import com.krzykrucz.elesson.currentlesson.domain.topic.PersistInProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.topic.chooseTopic
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter


internal fun topicRestAdapter(
    checkIfAttendanceIsChecked: CheckIfAttendanceIsChecked,
    fetchFinishedLessons: CountFinishedLessons,
    persistInProgressLesson: PersistInProgressLesson
) = coRouter {
    POST("/topic") { request ->
        val chooseTopicDto = request.awaitBody<ChooseTopicDto>()
        chooseTopic(
            checkIfAttendanceIsChecked,
            chooseTopicDto.topicTitle,
            fetchFinishedLessons,
            chooseTopicDto.lessonIdentifier
        )
            .asyncDoIfRight { inProgressLesson -> persistInProgressLesson(chooseTopicDto.lessonIdentifier, inProgressLesson) }
            .toServerResponse()
    }
}

private data class ChooseTopicDto(
    val lessonIdentifier: LessonIdentifier,
    val topicTitle: TopicTitle
)
