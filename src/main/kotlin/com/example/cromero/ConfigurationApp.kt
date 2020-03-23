package com.example.cromero


import org.javers.core.JaversBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.reactive.function.client.WebClient

@EnableTransactionManagement
@Configuration
class ConfigurationApp {


    @Bean
    fun mongoTransactionManager(dbFactory: ReactiveMongoDatabaseFactory): ReactiveMongoTransactionManager {
        return ReactiveMongoTransactionManager(dbFactory)
    }

    @Bean
    fun webClient() =WebClient.create("http://localhost:8082");

}