package com.krzykrucz.elesson.currentlesson.adapters

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchUnitRunner
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.junit.runner.RunWith

@RunWith(ArchUnitRunner::class)
@AnalyzeClasses(packagesOf = [ArchitectureTest::class], importOptions = [DoNotIncludeTests::class])
class ArchitectureTest {

    @ArchTest
    val `one adapter should not access another adapter` =
            slices()
                    .matching("..adapters.(*)")
                    .should().notDependOnEachOther()

    @ArchTest
    val `domain should not access adapters` =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should()
                    .accessClassesThat().resideInAPackage("..adapters..")


    @ArchTest
    val `domain should not access infrastructure` =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should()
            .accessClassesThat().resideInAPackage("..infrastructure..")

}