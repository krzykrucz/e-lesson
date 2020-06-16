package com.krzykrucz.elesson.currentlesson.shared

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

typealias RestApi = suspend (ServerRequest) -> ServerResponse
