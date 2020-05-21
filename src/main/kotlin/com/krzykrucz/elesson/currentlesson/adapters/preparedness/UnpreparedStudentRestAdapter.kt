package com.krzykrucz.elesson.currentlesson.adapters.preparedness

import com.krzykrucz.elesson.currentlesson.domain.preparedness.ReportUnpreparedStudentApi
import com.krzykrucz.elesson.currentlesson.domain.preparedness.StudentReportingUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.renderAndAwait

data class ReportUnpreparedRequest(
    val studentName: StudentReportingUnpreparedness,
    val lessonIdentifier: LessonIdentifier
)

@Configuration
class ReportUnpreparedRouteAdapter {

    @Bean
    fun reportUnpreparedRoute(reportUnpreparedStudentApi: ReportUnpreparedStudentApi) =
        coRouter {
            POST("/unprepared") { request ->
                request.awaitBody<ReportUnpreparedRequest>()
                    .let { (student, id) -> reportUnpreparedStudentApi(id, student) }
                    .fold({
                        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .renderAndAwait(it.javaClass.simpleName)
                    }, {
                        ServerResponse.ok().buildAndAwait()
                    })
            }
        }

}
