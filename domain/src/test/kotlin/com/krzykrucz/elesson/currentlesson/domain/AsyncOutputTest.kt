package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.Either
import arrow.effects.IO
import org.junit.Test
import kotlin.test.assertEquals

class AsyncOutputTest {

    @Test
    fun testFlatMapping1() {
        //given
        val asyncOutput1: AsyncOutput<Int> = IO.just(Either.right(1))
        val asyncOutput2: (Int) -> AsyncOutput<Int> = { IO.just(Either.right(it + 1)) }
        //when
        val finalOutput: AsyncOutput<Int> = asyncOutput1.flatMapIfSuccess(asyncOutput2)
        //then
        assertEquals(Either.right(2), finalOutput.unsafeRunSync())
    }

    @Test
    fun testFlatMapping2() {
        //given
        val asyncOutputWithDomainError: AsyncOutput<Int> = IO.just(Either.left(LessonError.NotScheduledLesson()))
        val asyncOutput2: (Int) -> AsyncOutput<Int> = { IO.just(Either.right(it + 1)) }
        //when
        val finalOutput: AsyncOutput<Int> = asyncOutputWithDomainError.flatMapIfSuccess(asyncOutput2)
        //then
        assertEquals(Either.left(LessonError.NotScheduledLesson()), finalOutput.unsafeRunSync())
    }

    @Test
    fun testFlatMapping3() {
        //given
        val asyncOutput1: AsyncOutput<Int> = IO.just(Either.right(1))
        val asyncOutput2: (Int) -> AsyncOutput<Int> = { IO.just(Either.left(LessonError.NotScheduledLesson())) }
        //when
        val finalOutput: AsyncOutput<Int> = asyncOutput1.flatMapIfSuccess(asyncOutput2)
        //then
        assertEquals(Either.left(LessonError.NotScheduledLesson()), finalOutput.unsafeRunSync())
    }

    @Test
    fun testMapping() {

    }

    @Test
    fun testFailing() {

    }

}