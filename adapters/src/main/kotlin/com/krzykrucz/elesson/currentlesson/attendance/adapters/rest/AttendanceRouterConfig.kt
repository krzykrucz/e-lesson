package com.krzykrucz.elesson.currentlesson.attendance.adapters.rest

import com.krzykrucz.elesson.currentlesson.attendance.domain.PersistAttendance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class AttendanceRouterConfig {

    @Bean
    fun attendanceRouter(
            persistAttendance: PersistAttendance
    ) = router {
        (path("/attendance") and accept(MediaType.APPLICATION_JSON)).nest {
            POST("/absent", handleNoteAbsentRequest(persistAttendance))
            POST("/present", handleNotePresentRequest(persistAttendance))
            POST("/late", handleNoteLateRequest(persistAttendance))
            GET("", handleGetAttendanceRequest())
        }
    }
}