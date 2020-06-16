package com.krzykrucz.elesson.currentlesson.startlesson

import com.krzykrucz.elesson.currentlesson.startlesson.rest.startLessonRestAdapter
import org.springframework.context.support.beans

val startLessonAdapters = beans {
    bean {
        startLessonRestAdapter(
            checkScheduledLessonAdapter,
            fetchClassRegistryAdapter,
            startedLessonPersistenceAdapter
        )
    }
}
