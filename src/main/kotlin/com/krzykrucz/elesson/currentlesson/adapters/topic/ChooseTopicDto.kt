package com.krzykrucz.elesson.currentlesson.adapters.topic

import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.TopicTitle

data class ChooseTopicDto(val lessonIdentifier: LessonIdentifier, val topicTitle: TopicTitle)
