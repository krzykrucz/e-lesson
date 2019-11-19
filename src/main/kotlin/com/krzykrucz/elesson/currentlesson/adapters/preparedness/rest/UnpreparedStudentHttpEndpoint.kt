package com.krzykrucz.elesson.currentlesson.adapters.preparedness.rest

import com.krzykrucz.elesson.currentlesson.adapters.flattenAsyncOutput
import com.krzykrucz.elesson.currentlesson.adapters.handleErrors
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.ReportUnpreparedStudentApi
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentReportingUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

data class ReportUnpreparedRequest(
    val studentName: StudentReportingUnpreparedness,
    val lessonIdentifier: LessonIdentifier
)

@Configuration
class ReportUnpreparedRouteAdapter {

    @Bean
    fun reportUnpreparedRoute(reportUnpreparedStudentApi: ReportUnpreparedStudentApi) =
        router {
            POST("/unprepared") { request ->
                request.bodyToMono(ReportUnpreparedRequest::class.java)
                    .map { (student, id) -> reportUnpreparedStudentApi(id, student) }
                    .flattenAsyncOutput()
                    .flatMap { ServerResponse.ok().build() }
                    .handleErrors()
            }
        }

}
