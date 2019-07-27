package com.krzykrucz.elesson.currentlesson

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ELessonApplication

fun main(args: Array<String>) {
    runApplication<ELessonApplication>(*args)
}
