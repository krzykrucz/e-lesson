package com.krzykrucz.elesson.currentlesson.adapters

import arrow.core.NonEmptyList
import com.krzykrucz.elesson.currentlesson.domain.ClassGroupName
import com.krzykrucz.elesson.currentlesson.domain.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.FirstName
import com.krzykrucz.elesson.currentlesson.domain.PupilEntry
import com.krzykrucz.elesson.currentlesson.domain.Register
import com.krzykrucz.elesson.currentlesson.domain.RegisterOrdinal
import com.krzykrucz.elesson.currentlesson.domain.SecondName
import com.virtuslab.basetypes.refined.NaturalNumber
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.arrow.toAsync
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


class FetchClassRegistryAdapter : FetchClassRegistry {

    override fun invoke(className: ClassGroupName) =
        Register(
            NonEmptyList.invoke(
                studentRecord(RegisterOrdinal(NaturalNumber.ONE), "Harry", "Potter"),
                studentRecord(RegisterOrdinal(NaturalNumber.TWO), "Hermione", "Granger")
            )
        )
            .let { Result.success(it) }
            .toAsync()

}

fun studentRecord(ordinal: RegisterOrdinal, firstName: String, secondName: String): PupilEntry =
    PupilEntry(
        ordinal,
        FirstName(NonEmptyText.of(firstName).orNull()!!),
        SecondName(NonEmptyText.of(secondName).orNull()!!)
    )

@Configuration
class FetchClassRegistryAdapterConfig {
    @Bean
    fun fetchClassRegistryAdapter(): FetchClassRegistry = FetchClassRegistryAdapter()
}