package com.krzykrucz.elesson.currentlesson

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchUnitRunner
import com.tngtech.archunit.library.Architectures
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.junit.runner.RunWith

@RunWith(ArchUnitRunner::class)
@AnalyzeClasses(packagesOf = [ArchitectureTest::class], importOptions = [DoNotIncludeTests::class])
class ArchitectureTest {
    @ArchTest
    val `there are no package cycles` =
            slices()
                    .matching("$BASE_PACKAGE.(**)..")
                    .should()
                    .beFreeOfCycles()

    @ArchTest
    val `one adapter should not access another adapter` =
            slices()
                    .matching("..adapters.(*)")
                    .should().notDependOnEachOther()

    @ArchTest
    val `one domain should not access another domain` =
            slices()
                    .matching("..domain.(*)")
                    .should().notDependOnEachOther()

    @ArchTest
    val `should keep ports adapters dependencies` =
            Architectures.layeredArchitecture()
                    .layer("AttendanceDomain").definedBy("..attendance.domain..")
                    .layer("AttendanceAdapters").definedBy("..attendance.adapters..")
                    .layer("StartLessonDomain").definedBy("..startlesson.domain..")
                    .layer("StartLessonAdapters").definedBy("..startlesson.adapters..")
                    .layer("Monolith").definedBy("..monolith..")
                    .whereLayer("AttendanceAdapters").mayNotBeAccessedByAnyLayer()
                    .whereLayer("StartLessonAdapters").mayNotBeAccessedByAnyLayer()
                    .whereLayer("AttendanceDomain").mayOnlyBeAccessedByLayers("AttendanceAdapters", "Monolith")
                    .whereLayer("StartLessonDomain").mayOnlyBeAccessedByLayers("StartLessonAdapters", "Monolith")

    companion object {
        private val BASE_PACKAGE = ArchitectureTest::class.java.`package`.name
    }

}