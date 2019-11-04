package com.krzykrucz.elesson.currentlesson.lessonprogress.adapters.rest

import com.krzykrucz.elesson.currentlesson.lessonprogress.usecase.LoadLessonProgress
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class LessonProgressRouteConfig {

    @Bean
    fun lessonProgressRouter(loadLessonProgress: LoadLessonProgress) = router {
        (path("/progress") and accept(MediaType.APPLICATION_JSON)).nest {
            GET("", handleLessonProgressViewRequest(loadLessonProgress))
        }
    }
}
