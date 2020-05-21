package com.krzykrucz.elesson.currentlesson.adapters.finishlesson

import com.krzykrucz.elesson.currentlesson.domain.finishlesson.finishLessonWorkflow
import org.springframework.context.support.beans

val finishLessonAdapters = beans {
    bean {
        finishLessonRestAdapter(
            finishLessonWorkflow(),
            storeLessonAsFinishedAdapter
        )
    }
}
