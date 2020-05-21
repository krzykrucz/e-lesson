package com.krzykrucz.elesson.currentlesson.adapters.topic

import org.springframework.context.support.beans

val topicAdapters = beans {
    bean {
        topicRestAdapter(
            checkIfAttendanceIsChecked,
            fetchFinishedLessonsCount,
            persistInProgressLesson
        )
    }
}
