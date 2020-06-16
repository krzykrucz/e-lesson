package com.krzykrucz.elesson.currentlesson

import com.krzykrucz.elesson.currentlesson.finishlesson.finishLessonAdapters
import com.krzykrucz.elesson.currentlesson.startlesson.startLessonAdapters
import com.krzykrucz.elesson.currentlesson.topic.topicAdapters
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext

@SpringBootApplication
class ELessonApplication

fun main(args: Array<String>) {
    runApplication<ELessonApplication>(*args)
}

object BeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) {
        startLessonAdapters.initialize(context)
        topicAdapters.initialize(context)
        finishLessonAdapters.initialize(context)
    }
}
