package com.example.cromero

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import reactor.core.publisher.Mono

@ControllerAdvice
@ResponseBody
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun webExchangeBindException(ex: Exception): Mono<Map<String, String?>> {
        return Mono.just(mapOf("error" to ex.message))
    }



}