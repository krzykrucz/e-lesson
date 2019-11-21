package com.krzykrucz.elesson.currentlesson.adapters

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceSpec extends Specification {
    @Autowired
    protected TestRestTemplate rest

}
