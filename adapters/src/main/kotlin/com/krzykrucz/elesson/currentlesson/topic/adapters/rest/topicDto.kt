package com.krzykrucz.elesson.currentlesson.topic.adapters.rest

import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.topic.domain.TopicTitle

data class ChooseTopicDto(val lessonIdentifier: LessonIdentifier, val topicTitle: TopicTitle)