package com.krzykrucz.elesson.currentlesson.finishlesson

import org.springframework.context.support.beans

val finishLessonAdapters = beans {
    bean {
        finishLessonRestAdapter(
            finishLessonWorkflow(),
            storeLessonAsFinishedAdapter
        )
    }
}
