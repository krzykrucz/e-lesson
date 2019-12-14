package com.krzykrucz.elesson.currentlesson.adapters

import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.domain.ClassName
import com.krzykrucz.elesson.currentlesson.domain.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.StartLessonError
import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import com.virtuslab.basetypes.result.Result
import kotlin.jvm.functions.Function1
import org.spockframework.spring.SpringBean
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

import static com.krzykrucz.elesson.currentlesson.domain.StartLessonError.ClassRegistryUnavailable
import static com.krzykrucz.elesson.currentlesson.domain.StartLessonError.ExternalError

class StartLessonFailedAcceptanceSpec extends AcceptanceSpec {

    @SpringBean
    Function1<ClassName, IO<Result<ClassRegistry, StartLessonError>>> fetchClassRegistry = Mock()

    def "should not a start lesson"() {
        given:
        1 * fetchClassRegistry._ >> new IO.Companion().just(new Result.Companion().error(error))

        when: 'Dark Arts classes started by Severus Snape'
        def response = rest.exchange(
                "/startlesson",
                HttpMethod.POST,
                new HttpEntity<>(new StartLessonRequest('Severus Snape')),
                String
        )

        then:
        response.statusCodeValue == statusCode
        response.body == message

        and:
        Database.LESSON_DATABASE.isEmpty()

        where:
        error                          || statusCode | message
        new ClassRegistryUnavailable() || 400        | 'ClassRegistryUnavailable'
        new ExternalError()            || 500        | 'ExternalError'
    }

}