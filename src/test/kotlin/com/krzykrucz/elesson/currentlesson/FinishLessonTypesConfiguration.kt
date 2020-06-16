package com.krzykrucz.elesson.currentlesson

import arrow.core.Either
import arrow.core.getOrElse
import com.krzykrucz.elesson.currentlesson.finishlesson.FinishLessonError
import com.krzykrucz.elesson.currentlesson.finishlesson.FinishLessonTime
import com.krzykrucz.elesson.currentlesson.finishlesson.FinishedLesson
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.isError
import io.cucumber.core.api.TypeRegistry
import io.cucumber.core.api.TypeRegistryConfigurer
import io.cucumber.cucumberexpressions.ParameterType
import java.util.Locale
import java.util.Locale.ENGLISH


sealed class MatchFinishLessonResult {

    abstract fun matches(
        inProgressLesson: InProgressLesson,
        finishLessonResult: Either<FinishLessonError, FinishedLesson>
    ): Boolean

    object Error : MatchFinishLessonResult() {
        override fun matches(
            inProgressLesson: InProgressLesson,
            finishLessonResult: Either<FinishLessonError, FinishedLesson>
        ): Boolean = finishLessonResult.isError()

    }

    object Success : MatchFinishLessonResult() {
        override fun matches(
            inProgressLesson: InProgressLesson,
            finishLessonResult: Either<FinishLessonError, FinishedLesson>
        ): Boolean =
            finishLessonResult.map {
                it == FinishedLesson(inProgressLesson.lessonTopic)
            }.getOrElse { false }
    }
}

class FinishLessonTypesConfiguration : TypeRegistryConfigurer {
    override fun locale(): Locale = ENGLISH

    override fun configureTypeRegistry(registry: TypeRegistry) =
        registry.run {
            defineCurrentTime()
            defineFinishLessonResultMatch()
        }

    private fun TypeRegistry.defineCurrentTime() =
        defineParameterType(
            ParameterType<FinishLessonTime>(
                "currentTime",
                "\\d\\d:\\d\\d:\\d\\d",
                FinishLessonTime::class.java
            ) { param: String ->
                FinishLessonTime.parse(param)
            }
        )

    private fun TypeRegistry.defineFinishLessonResultMatch() =
        defineParameterType(
            ParameterType<MatchFinishLessonResult>(
                "finished",
                "not finished|finished",
                MatchFinishLessonResult::class.java
            ) { param: String ->
                when (param) {
                    "not finished" -> MatchFinishLessonResult.Error
                    "finished" -> MatchFinishLessonResult.Success
                    else -> throw IllegalArgumentException("Cannot parse param $param to FinishedLessonResult")
                }
            }
        )
}
