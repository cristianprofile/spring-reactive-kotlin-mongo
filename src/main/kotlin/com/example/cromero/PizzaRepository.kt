package com.example.cromero

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono


@Repository
interface PizzaRepository : ReactiveCrudRepository<Pizza, Long> {
    fun findByName(name: String): Mono<Pizza>
    fun existsByNameNot(name: String): Mono<Boolean>
    fun findByDescription(name: String): Mono<Pizza>
    fun existsByDescriptionNot(name: String): Mono<Boolean>
}