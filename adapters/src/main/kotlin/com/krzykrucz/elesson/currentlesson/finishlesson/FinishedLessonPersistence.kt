package com.krzykrucz.elesson.currentlesson.finishlesson

import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.FinishedLesson
import io.cucumber.java.lv.Un


fun storeFinishedLesson(finishedLesson: FinishedLesson) : IO<Unit>