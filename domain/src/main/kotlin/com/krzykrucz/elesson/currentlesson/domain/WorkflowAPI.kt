package com.krzykrucz.elesson.currentlesson.domain


sealed class Result {
    object Success : Result()
    object Failure : Result()
}