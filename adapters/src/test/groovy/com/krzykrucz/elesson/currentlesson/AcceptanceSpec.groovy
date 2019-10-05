package com.krzykrucz.elesson.currentlesson

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AcceptanceSpec extends Specification {


    protected TestRestTemplate rest
    protected String serverUrl

    void setup() {
        serverUrl = "http://localhost:8081"
        rest = new TestRestTemplate()
    }
}
