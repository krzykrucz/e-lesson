package com.krzykrucz.elesson.currentlesson

import arrow.core.getOrElse
import arrow.fx.typeclasses.seconds
import com.virtuslab.basetypes.result.arrow.AsyncResult
import junit.framework.Assert.assertTrue
import kotlin.test.assertEquals

fun <T : Any, E : Exception> AsyncResult<T, E>.test(): AsyncResultTester<T, E> = AsyncResultTester(this)

class AsyncResultTester<T : Any, E : Exception>(private val async: AsyncResult<T, E>) {

    fun assertSuccess(t: T): AsyncResultTester<T, E> {
        assertEquals(
            async.unsafeRunTimed(5.seconds)
                .getOrElse { null }
                ?.component1(),
            t
        )
        return this
    }

    fun assertFailure(e: E): AsyncResultTester<T, E> {
        assertEquals(
            async.unsafeRunTimed(5.seconds)
                .getOrElse { null }
                ?.component2(),
            e
        )
        return this
    }

    fun assertThatSuccess(predicate: (T) -> Boolean): AsyncResultTester<T, E> {
        assertTrue(
            async.unsafeRunTimed(5.seconds)
                .getOrElse { null }
                ?.component1()
                ?.let(predicate) ?: false
        )
        return this
    }

    fun assertError(ex: Throwable): AsyncResultTester<T, E> {
        assertEquals(
            async.attempt().unsafeRunTimed(5.seconds)
                .getOrElse { null }
                ?.swap()
                ?.toOption()
                ?.getOrElse { null },
            ex
        )
        return this
    }
}