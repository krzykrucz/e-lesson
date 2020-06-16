package com.krzykrucz.elesson.currentlesson.topic

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
