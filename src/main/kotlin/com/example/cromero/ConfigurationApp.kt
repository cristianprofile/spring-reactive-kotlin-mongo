package com.example.cromero


import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import java.nio.charset.Charset


@Configuration
class ConfigurationApp {


    @Bean
    fun easyRandom(): EasyRandom {
        return EasyRandom(EasyRandomParameters()
                .seed(123L)
                .objectPoolSize(100)
                .randomizationDepth(3)
                .charset(Charset.forName("UTF-8"))
                .stringLengthRange(4, 10)
                .collectionSizeRange(1, 3))
    }

    @Bean
    fun webClient() =WebClient.create("http://localhost:8082");

}
