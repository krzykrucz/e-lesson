package com.krzykrucz.elesson.currentlesson.domain

import com.virtuslab.basetypes.result.arrow.AsyncResult
import com.virtuslab.basetypes.result.pairWith

fun <T : Any, E : Any, S : Any> AsyncResult<T, E>.pairWith(s: S): AsyncResult<Pair<T, S>, E> =
    this.map {
        it.pairWith(s)
    }