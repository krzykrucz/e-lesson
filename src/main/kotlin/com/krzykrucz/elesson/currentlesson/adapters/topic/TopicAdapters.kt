package com.krzykrucz.elesson.currentlesson.adapters.topic

import com.krzykrucz.elesson.currentlesson.domain.topic.chooseTopicWorkflow
import org.springframework.context.support.beans

val topicAdapters = beans {
    bean {
        topicRestAdapter(
            chooseTopicWorkflow(
                checkIfAttendanceIsChecked,
                fetchFinishedLessonsCount
            ),
            persistInProgressLesson
        )
    }
}
