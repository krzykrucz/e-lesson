package com.krzykrucz.elesson.currentlesson

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.AsyncFactory
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.failIf
import com.krzykrucz.elesson.currentlesson.shared.flatMapSuccess
import com.krzykrucz.elesson.currentlesson.shared.mapSuccess
import org.junit.Test
import kotlin.test.assertEquals

class AsyncOutputTest {

    private val sampleError = Error()

    @Test
    fun shouldFlatMapSuccessToSuccess() {
        //given
        val asyncIntOf1: AsyncOutput<Int, Error> = AsyncFactory.justSuccess(1)
        val addOneAsync: (Int) -> AsyncOutput<Int, Error> = { AsyncFactory.justSuccess(it + 1) }

        //when
        val finalOutput: AsyncOutput<Int, Error> = asyncIntOf1.flatMapSuccess(addOneAsync)

        //then
        assertEquals(Either.right(2), finalOutput.unsafeRunSync())
    }

    @Test
    fun shouldFlatMapSuccessFromError() {
        //given
        val asyncOutputWithDomainError: AsyncOutput<Int, Error> = AsyncFactory.justError(sampleError)
        val asyncOutputProvider: (Int) -> AsyncOutput<Int, Error> = { AsyncFactory.justSuccess(it + 1) }

        //when
        val finalOutput: AsyncOutput<Int, Error> = asyncOutputWithDomainError.flatMapSuccess(asyncOutputProvider)

        //then
        assertEquals(Either.left(sampleError), finalOutput.unsafeRunSync())
    }

    @Test
    fun shouldFlatMapSuccessToError() {
        //given
        val asyncIntOf1: AsyncOutput<Int, Error> = AsyncFactory.justSuccess(1)
        val asyncErrorProvider: (Int) -> AsyncOutput<Int, Error> = { AsyncFactory.justError(sampleError) }

        //when
        val finalOutput: AsyncOutput<Int, Error> = asyncIntOf1.flatMapSuccess(asyncErrorProvider)

        //then
        assertEquals(Either.left(sampleError), finalOutput.unsafeRunSync())
    }

    @Test
    fun testMapping() {
        //given
        val asyncIntOf1: AsyncOutput<Int, Error> = AsyncFactory.justSuccess(1)

        //when
        val finalOutput: AsyncOutput<Int, Error> = asyncIntOf1.mapSuccess { it + 1 }

        //then
        assertEquals(Either.right(2), finalOutput.unsafeRunSync())
    }

    @Test
    fun testFailing() {
        //given
        val asyncIntOf1: AsyncOutput<Int, Error> = AsyncFactory.justSuccess(1)

        //when
        val finalOutput: AsyncOutput<Int, Error> = asyncIntOf1.failIf({ true }, sampleError)

        //then
        assertEquals(Either.left(sampleError), finalOutput.unsafeRunSync())
    }

}