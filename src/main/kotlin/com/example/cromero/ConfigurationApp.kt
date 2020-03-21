package com.example.cromero


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import java.nio.charset.Charset


@Configuration
class ConfigurationApp {

    @Bean
    fun webClient() =WebClient.create("http://localhost:8082");

}
