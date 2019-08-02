package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.Option

fun startLesson(): StartLesson {
    return { fetchClassRegistry, checkScheduledLesson, teacher, localDateTime ->
        Option.empty()
    }
}