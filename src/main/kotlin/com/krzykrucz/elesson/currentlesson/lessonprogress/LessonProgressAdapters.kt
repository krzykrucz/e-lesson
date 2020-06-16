package com.krzykrucz.elesson.currentlesson.lessonprogress

import org.springframework.context.support.beans

val lessonProgressAdapters = beans {
    bean { lessonProgressRouter(loadLessonProgressAdapter()) }
}
