package com.krzykrucz.elesson.currentlesson.attendance.adapters.rest

import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchCheckedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchNotCompletedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.PersistAttendance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class AttendanceRouterConfig {

    @Bean
    fun attendanceRouter(
            persistAttendance: PersistAttendance,
            fetchNotCompletedAttendance: FetchNotCompletedAttendance,
            fetchCheckedAttendance: FetchCheckedAttendance
    ) = router {
        (path("/attendance") and accept(MediaType.APPLICATION_JSON)).nest {
            POST("/absent", handleNoteAbsentRequest(persistAttendance, fetchNotCompletedAttendance))
            POST("/present", handleNotePresentRequest(persistAttendance, fetchNotCompletedAttendance))
            POST("/late", handleNoteLateRequest(persistAttendance, fetchCheckedAttendance))
            GET("", handleGetAttendanceRequest())
        }
    }
}