package com.krzykrucz.elesson.currentlesson.preparedness.adapters.rest

import com.krzykrucz.elesson.currentlesson.flattenAsyncOutput
import com.krzykrucz.elesson.currentlesson.handleErrors
import com.krzykrucz.elesson.currentlesson.preparedness.domain.ReportUnpreparedStudentApi
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.StudentReportingUnpreparedness
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
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
