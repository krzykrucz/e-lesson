package com.krzykrucz.elesson.currentlesson.finishlesson

import arrow.core.Either
import arrow.core.getOrElse
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.CurrentHour
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.FinishLessonError
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.FinishedLesson
import com.krzykrucz.elesson.currentlesson.shared.isError
import com.krzykrucz.elesson.currentlesson.topic.domain.InProgressLesson
import io.cucumber.core.api.TypeRegistry
import io.cucumber.core.api.TypeRegistryConfigurer
import io.cucumber.cucumberexpressions.ParameterType
import java.util.*
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
            defineCurrentHour()
            defineFinishLessonResultMatch()
        }

    private fun TypeRegistry.defineCurrentHour() =
        defineParameterType(
            ParameterType<CurrentHour>(
                "currentHour",
                "\\d\\d:\\d\\d:\\d\\d",
                CurrentHour::class.java
            ) { param: String ->
                CurrentHour.parse(param)
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