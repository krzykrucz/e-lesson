package com.krzykrucz.elesson.currentlesson.attendance

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class AttendanceWorkflowAdapter {

    @Bean
    fun attendanceRouter() = router {
        (path("/attendance") and accept(MediaType.APPLICATION_JSON)).nest {
            POST("/absent", handleNoteAbsentRequest())
            POST("/present", handleNotePresentRequest())
            POST("/late", handleNoteLateRequest())
            GET("", handleGetAttendanceRequest())
            POST("", handleFinishAttendanceRequest())
        }
    }
}