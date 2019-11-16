package com.krzykrucz.elesson.currentlesson

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.AsyncFactory
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.Output
import com.krzykrucz.elesson.currentlesson.shared.failIf
import com.krzykrucz.elesson.currentlesson.shared.flatMapAsyncSuccess
import com.krzykrucz.elesson.currentlesson.shared.flatMapSuccess
import com.krzykrucz.elesson.currentlesson.shared.handleError
import com.krzykrucz.elesson.currentlesson.shared.mapSuccess
import org.junit.Test
import kotlin.test.assertEquals

class AsyncOutputTest {

    private val sampleError = Error()

    @Test
    fun shouldFlatMapAsyncSuccessToSuccess() {
        //given
        val asyncIntOf1: AsyncOutput<Error, Int> = AsyncFactory.justSuccess(1)
        val addOneAsync: (Int) -> AsyncOutput<Error, Int> = { AsyncFactory.justSuccess(it + 1) }

        //when
        val finalOutput: AsyncOutput<Error, Int> = asyncIntOf1.flatMapAsyncSuccess(addOneAsync)

        //then
        assertEquals(Either.right(2), finalOutput.unsafeRunSync())
    }

    @Test
    fun shouldFlatMapAsyncSuccessFromError() {
        //given
        val asyncOutputWithDomainError: AsyncOutput<Error, Int> = AsyncFactory.justError(sampleError)
        val asyncOutputProvider: (Int) -> AsyncOutput<Error, Int> = { AsyncFactory.justSuccess(it + 1) }

        //when
        val finalOutput: AsyncOutput<Error, Int> = asyncOutputWithDomainError.flatMapAsyncSuccess(asyncOutputProvider)

        //then
        assertEquals(Either.left(sampleError), finalOutput.unsafeRunSync())
    }

    @Test
    fun shouldFlatMapAsyncSuccessToError() {
        //given
        val asyncIntOf1: AsyncOutput<Error, Int> = AsyncFactory.justSuccess(1)
        val asyncErrorProvider: (Int) -> AsyncOutput<Error, Int> = { AsyncFactory.justError(sampleError) }

        //when
        val finalOutput: AsyncOutput<Error, Int> = asyncIntOf1.flatMapAsyncSuccess(asyncErrorProvider)

        //then
        assertEquals(Either.left(sampleError), finalOutput.unsafeRunSync())
    }

    @Test
    fun testMapping() {
        //given
        val asyncIntOf1: AsyncOutput<Error, Int> = AsyncFactory.justSuccess(1)

        //when
        val finalOutput: AsyncOutput<Error, Int> = asyncIntOf1.mapSuccess { it + 1 }

        //then
        assertEquals(Either.right(2), finalOutput.unsafeRunSync())
    }

    @Test
    fun testFailing() {
        //given
        val asyncIntOf1: AsyncOutput<Error, Int> = AsyncFactory.justSuccess(1)

        //when
        val finalOutput: AsyncOutput<Error, Int> = asyncIntOf1.failIf({ true }, sampleError)

        //then
        assertEquals(Either.left(sampleError), finalOutput.unsafeRunSync())
    }

    @Test
    fun shouldHandleError() {
        //given
        val asyncIntWithError: AsyncOutput<Error, Int> = AsyncFactory.justError(sampleError)

        //when
        val finalOutput: AsyncOutput<Error, Int> = asyncIntWithError.handleError { 1 }

        //then
        assertEquals(Either.right(1), finalOutput.unsafeRunSync())
    }


    @Test
    fun shouldFlatMapSuccessToSuccess() {
        //given
        val asyncIntOf1: AsyncOutput<Error, Int> = AsyncFactory.justSuccess(1)
        val addOne: (Int) -> Output<Error, Int> = { Output.right(it + 1) }

        //when
        val finalOutput: AsyncOutput<Error, Int> = asyncIntOf1.flatMapSuccess(addOne)

        //then
        assertEquals(Either.right(2), finalOutput.unsafeRunSync())
    }

    @Test
    fun shouldFlatMapSuccessFromError() {
        //given
        val asyncOutputWithDomainError: AsyncOutput<Error, Int> = AsyncFactory.justError(sampleError)
        val addOne: (Int) -> Output<Error, Int> = { Output.right(it + 1) }

        //when
        val finalOutput: AsyncOutput<Error, Int> = asyncOutputWithDomainError.flatMapSuccess(addOne)

        //then
        assertEquals(Either.left(sampleError), finalOutput.unsafeRunSync())
    }

    @Test
    fun shouldFlatMapSuccessToError() {
        //given
        val asyncIntOf1: AsyncOutput<Error, Int> = AsyncFactory.justSuccess(1)
        val errorProvider: (Int) -> Output<Error, Int> = { Output.left(sampleError) }

        //when
        val finalOutput: AsyncOutput<Error, Int> = asyncIntOf1.flatMapSuccess(errorProvider)

        //then
        assertEquals(Either.left(sampleError), finalOutput.unsafeRunSync())
    }

}