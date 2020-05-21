package com.krzykrucz.elesson.currentlesson.adapters.startlesson

import com.krzykrucz.elesson.currentlesson.adapters.startlesson.rest.startLessonRestAdapter
import com.krzykrucz.elesson.currentlesson.domain.startlesson.startLesson
import org.springframework.context.support.beans

val startLessonAdapters = beans {
    bean {
        startLessonRestAdapter(
            startedLessonPersistenceAdapter,
            startLesson(
                checkScheduledLessonAdapter,
                fetchClassRegistryAdapter
            )
        )
    }
}
