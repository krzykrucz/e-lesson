package com.krzykrucz.elesson.currentlesson.finishlesson.domain

import arrow.core.Either


fun bellRang() : BellRang = {lessonHourNumber, currentHour -> throw NotImplementedError() }

fun finishLesson(bellRang: BellRang) : FinishLesson = {inProgressLesson, currentHour -> throw NotImplementedError()}