package com.krzykrucz.elesson.currentlesson.topic

import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.topic.domain.TopicTitle

data class ChooseTopicDto(val lessonIdentifier: LessonIdentifier, val topicTitle: TopicTitle, val date: String)
