package com.krzykrucz.elesson.currentlesson.attendance.adapters.rest

import arrow.core.getOrElse
import arrow.fx.IO
import arrow.fx.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.attendance.adapters.usecase.handleGetAttendanceRequest
import com.krzykrucz.elesson.currentlesson.attendance.adapters.usecase.handleLateAttendanceDto
import com.krzykrucz.elesson.currentlesson.attendance.adapters.usecase.handleNoteAbsentDto
import com.krzykrucz.elesson.currentlesson.attendance.adapters.usecase.handleNotePresentDto
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

// TODO divide this config to 3 different adapters
@Configuration
class AttendanceRouterConfig {

    @Bean
    fun attendanceRouter() = router {
        (path("/attendance") and accept(MediaType.APPLICATION_JSON)).nest {
            POST("/absent") { request ->
                request.toAttendanceDto()
                    .flatMap { handleNoteAbsentDto(it).run() }
            }
            POST("/present") { request ->
                request.toAttendanceDto()
                    .flatMap { handleNotePresentDto(it).run() }
            }
            POST("/late") { request ->
                request.bodyToMono<LateAttendanceDto>()
                    .flatMap { handleLateAttendanceDto(it).run() }
            }
            GET("", handleGetAttendanceRequest())
        }
    }

    private fun ServerRequest.toAttendanceDto() = this.bodyToMono<AttendanceDto>()
    private fun IO<Mono<ServerResponse>>.run(): Mono<ServerResponse> =
        this.unsafeRunTimed(Duration(1, TimeUnit.SECONDS))
            .getOrElse { ServerResponse.badRequest().build() }
}
