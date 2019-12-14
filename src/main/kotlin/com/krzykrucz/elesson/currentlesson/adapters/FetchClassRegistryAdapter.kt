package com.krzykrucz.elesson.currentlesson.adapters

import arrow.core.getOrElse
import com.krzykrucz.elesson.currentlesson.domain.ClassName
import com.krzykrucz.elesson.currentlesson.domain.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.StartLessonError
import com.krzykrucz.elesson.currentlesson.domain.StudentList
import com.krzykrucz.elesson.currentlesson.domain.StudentRecord
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.arrow.toAsync
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


class FetchClassRegistryAdapter : FetchClassRegistry {

    override fun invoke(className: ClassName) =
        NonEmptyText.of("Gryffindor")
            .map(::ClassName)
            .map { clazz ->
                ClassRegistry(
                    clazz,
                    StudentList(studentRecord("Harry", "Potter"), studentRecord("Hermione", "Granger"))
                )
            }
            .map { Result.success(it) }
            .getOrElse { Result.error(StartLessonError.ExternalError) }
            .toAsync()

}

fun studentRecord(firstName: String, secondName: String): StudentRecord =
    StudentRecord(
        NonEmptyText.of(firstName).orNull()!!,
        NonEmptyText.of(secondName).orNull()!!
    )

@Configuration
class FetchClassRegistryAdapterConfig {
    @Bean
    fun fetchClassRegistryAdapter(): FetchClassRegistry = FetchClassRegistryAdapter()
}